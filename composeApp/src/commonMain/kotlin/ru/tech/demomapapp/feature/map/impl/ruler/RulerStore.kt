package ru.tech.demomapapp.feature.map.impl.ruler

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal interface RulerStore : Store<RulerStore.Intent, RulerStore.State, RulerStore.Label> {
    sealed interface Intent {
        object ToggleClicked : Intent
        data class LocationUpdated(val location: MapLocationMarker?) : Intent
        data class CameraSnapshotReceived(val snapshot: MapCameraSnapshot) : Intent
    }

    data class State(
        val isEnabled: Boolean = false,
        val currentLocation: MapLocationMarker? = null,
        val lastCameraSnapshot: MapCameraSnapshot? = null,
        val measurement: RulerMeasurement? = null,
        val infoWindow: RulerInfoWindowState? = null,
    ) {
        fun toModel(): RulerModel = RulerModel(
            isEnabled = isEnabled,
            measurement = measurement,
            infoWindow = infoWindow,
        )
    }

    sealed interface Message {
        data class EnabledUpdated(val isEnabled: Boolean) : Message
        data class LocationStored(val location: MapLocationMarker?) : Message
        data class CameraSnapshotStored(val snapshot: MapCameraSnapshot) : Message
        data class MeasurementUpdated(
            val measurement: RulerMeasurement,
            val infoWindow: RulerInfoWindowState,
        ) : Message

        object MeasurementCleared : Message
    }

    sealed interface Label {
        data class ViewportCommandRequested(val command: MapViewportCommand) : Label
    }
}
