package ru.tech.demomapapp.feature.map.impl.ruler

import com.arkivanov.mvikotlin.core.store.Executor
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator

internal class RulerExecutor(
    private val rulerMeasurementCalculator: RulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter,
) : Executor<RulerStore.Intent, Nothing, RulerStore.State, RulerStore.Message, RulerStore.Label> {

    private lateinit var callbacks: Executor.Callbacks<
        RulerStore.State,
        RulerStore.Message,
        Nothing,
        RulerStore.Label,
        >

    override fun init(
        callbacks: Executor.Callbacks<
            RulerStore.State,
            RulerStore.Message,
            Nothing,
            RulerStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: RulerStore.Intent) {
        when (intent) {
            is RulerStore.Intent.CameraSnapshotReceived -> handleCameraSnapshotReceived(intent.snapshot)
            is RulerStore.Intent.LocationUpdated -> handleLocationUpdated(intent.location)
            RulerStore.Intent.ToggleClicked -> handleToggleClicked()
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun handleToggleClicked() {
        val updatedState = currentState().copy(isEnabled = !currentState().isEnabled)
        callbacks.onMessage(RulerStore.Message.EnabledUpdated(updatedState.isEnabled))
        if (!updatedState.isEnabled) {
            callbacks.onMessage(RulerStore.Message.MeasurementCleared)
            return
        }
        updatedState.lastCameraSnapshot?.let { snapshot ->
            callbacks.onLabel(
                RulerStore.Label.ViewportCommandRequested(
                    MapViewportCommand.MoveTo(
                        latitude = snapshot.latitude,
                        longitude = snapshot.longitude,
                    ),
                ),
            )
        }
        publishMeasurement(updatedState)
    }

    private fun handleLocationUpdated(location: MapLocationMarker?) {
        val updatedState = currentState().copy(currentLocation = location)
        callbacks.onMessage(RulerStore.Message.LocationStored(location))
        publishMeasurement(updatedState)
    }

    private fun handleCameraSnapshotReceived(snapshot: ru.tech.demomapapp.feature.map.api.MapCameraSnapshot) {
        val updatedState = currentState().copy(lastCameraSnapshot = snapshot)
        callbacks.onMessage(RulerStore.Message.CameraSnapshotStored(snapshot))
        publishMeasurement(updatedState)
    }

    private fun publishMeasurement(state: RulerStore.State) {
        if (!state.isEnabled) {
            callbacks.onMessage(RulerStore.Message.MeasurementCleared)
            return
        }

        val snapshot = state.lastCameraSnapshot ?: run {
            callbacks.onMessage(RulerStore.Message.MeasurementCleared)
            return
        }
        val location = state.currentLocation ?: MapLocationMarker(
            latitude = snapshot.latitude,
            longitude = snapshot.longitude,
            isPlaceholder = true,
        )
        val measurement = rulerMeasurementCalculator.calculate(
            startLatitude = location.latitude,
            startLongitude = location.longitude,
            endLatitude = snapshot.latitude,
            endLongitude = snapshot.longitude,
        )
        callbacks.onMessage(
            RulerStore.Message.MeasurementUpdated(
                measurement = measurement,
                infoWindow = rulerInfoWindowStateFormatter.format(measurement),
            ),
        )
    }

    private fun currentState(): RulerStore.State = callbacks.state
}
