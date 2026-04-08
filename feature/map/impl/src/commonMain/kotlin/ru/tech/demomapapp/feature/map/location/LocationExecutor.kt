package ru.tech.demomapapp.feature.map.location

import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode

internal class LocationExecutor : com.arkivanov.mvikotlin.core.store.Executor<
    LocationStore.Intent,
    Nothing,
    LocationStore.State,
    LocationStore.Message,
    LocationStore.Label,
    > {

    private lateinit var callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
        LocationStore.State,
        LocationStore.Message,
        Nothing,
        LocationStore.Label,
        >

    override fun init(
        callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
            LocationStore.State,
            LocationStore.Message,
            Nothing,
            LocationStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: LocationStore.Intent) {
        when (intent) {
            is LocationStore.Intent.CameraSnapshotReceived -> callbacks.onMessage(
                LocationStore.Message.CameraSnapshotStored(intent.snapshot),
            )
            LocationStore.Intent.CurrentLocationFocusClicked -> handleCurrentLocationFocusClick()
            LocationStore.Intent.GpsToggled -> handleGpsToggle()
            is LocationStore.Intent.LocationResultReceived -> handleLocationResult(intent.result)
            LocationStore.Intent.LocationRequestConsumed -> callbacks.onMessage(
                LocationStore.Message.PendingRequestCleared,
            )
            LocationStore.Intent.MyLocationClicked -> handleMyLocationClick()
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun handleGpsToggle() {
        val state = callbacks.state
        if (state.pendingRequest == MapLocationRequest.EnableGpsLocationRequest) {
            callbacks.onMessage(LocationStore.Message.GpsDisabled)
            if (state.currentMarker != null) {
                callbacks.onLabel(LocationStore.Label.LocationUpdated(location = null))
            }
            return
        }

        when (state.mode) {
            MyLocationMode.GPS -> {
                callbacks.onMessage(LocationStore.Message.GpsDisabled)
                callbacks.onLabel(LocationStore.Label.LocationUpdated(location = null))
            }

            MyLocationMode.OFF,
            MyLocationMode.MANUAL_PLACEHOLDER,
            -> {
                callbacks.onMessage(LocationStore.Message.GpsEnableRequested)
                if (state.currentMarker != null) {
                    callbacks.onLabel(LocationStore.Label.LocationUpdated(location = null))
                }
                callbacks.onLabel(
                    LocationStore.Label.LocationRequestIssued(MapLocationRequest.EnableGpsLocationRequest),
                )
            }
        }
    }

    private fun handleMyLocationClick() {
        val state = callbacks.state
        if (state.mode == MyLocationMode.GPS) {
            return
        }

        val snapshot = state.lastCameraSnapshot ?: return
        val marker = MapLocationMarker(
            latitude = snapshot.latitude,
            longitude = snapshot.longitude,
            isPlaceholder = true,
        )
        callbacks.onMessage(LocationStore.Message.ManualPlaceholderSelected(marker))
        callbacks.onLabel(LocationStore.Label.LocationUpdated(marker))
    }

    private fun handleCurrentLocationFocusClick() {
        val state = callbacks.state
        val marker = state.currentMarker
        if (marker != null) {
            callbacks.onLabel(
                LocationStore.Label.ViewportCommandRequested(
                    MapViewportCommand.MoveTo(
                        latitude = marker.latitude,
                        longitude = marker.longitude,
                    ),
                ),
            )
            return
        }

        if (state.mode == MyLocationMode.GPS) {
            callbacks.onMessage(LocationStore.Message.RecenterRequestIssued)
            callbacks.onLabel(
                LocationStore.Label.LocationRequestIssued(MapLocationRequest.RecenterToGpsLocationRequest),
            )
        }
    }

    private fun handleLocationResult(result: LocationRequestResult) {
        val state = callbacks.state
        when (result) {
            LocationRequestResult.PermissionDenied -> {
                callbacks.onMessage(LocationStore.Message.LocationCleared)
                if (state.currentMarker != null) {
                    callbacks.onLabel(LocationStore.Label.LocationUpdated(location = null))
                }
            }

            LocationRequestResult.LocationUnavailable -> {
                if (state.mode == MyLocationMode.GPS &&
                    state.pendingRequest != MapLocationRequest.EnableGpsLocationRequest
                ) {
                    callbacks.onMessage(LocationStore.Message.PendingRequestCleared)
                } else {
                    callbacks.onMessage(LocationStore.Message.LocationCleared)
                    if (state.currentMarker != null) {
                        callbacks.onLabel(LocationStore.Label.LocationUpdated(location = null))
                    }
                }
            }

            is LocationRequestResult.LocationResolved -> {
                val marker = MapLocationMarker(
                    latitude = result.latitude,
                    longitude = result.longitude,
                    isPlaceholder = false,
                )
                callbacks.onMessage(LocationStore.Message.LocationResolved(marker))
                callbacks.onLabel(LocationStore.Label.LocationUpdated(marker))
                callbacks.onLabel(
                    LocationStore.Label.ViewportCommandRequested(
                        MapViewportCommand.MoveTo(
                            latitude = result.latitude,
                            longitude = result.longitude,
                        ),
                    ),
                )
            }
        }
    }
}
