package ru.tech.demomapapp.feature.map.impl.location

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode

internal interface LocationStore : Store<LocationStore.Intent, LocationStore.State, LocationStore.Label> {
    sealed interface Intent {
        object GpsToggled : Intent
        object MyLocationClicked : Intent
        object CurrentLocationFocusClicked : Intent
        data class LocationResultReceived(val result: LocationRequestResult) : Intent
        object LocationRequestConsumed : Intent
        data class CameraSnapshotReceived(val snapshot: MapCameraSnapshot) : Intent
    }

    data class State(
        val mode: MyLocationMode = MyLocationMode.OFF,
        val currentMarker: MapLocationMarker? = null,
        val pendingRequest: MapLocationRequest? = null,
        val hasRealLocation: Boolean = false,
        val lastCameraSnapshot: MapCameraSnapshot? = null,
    ) {
        fun toModel(): LocationModel = LocationModel(
            mode = mode,
            currentMarker = currentMarker,
            pendingRequest = pendingRequest,
        )

        companion object {
            fun fromModel(model: LocationModel): State = State(
                mode = model.mode,
                currentMarker = model.currentMarker,
                pendingRequest = model.pendingRequest,
                hasRealLocation = model.currentMarker?.isPlaceholder == false,
            )
        }
    }

    sealed interface Message {
        data class CameraSnapshotStored(val snapshot: MapCameraSnapshot) : Message
        object GpsEnableRequested : Message
        object GpsDisabled : Message
        data class ManualPlaceholderSelected(val marker: MapLocationMarker) : Message
        object RecenterRequestIssued : Message
        object PendingRequestCleared : Message
        data class LocationResolved(val marker: MapLocationMarker) : Message
        object LocationCleared : Message
    }

    sealed interface Label {
        data class LocationUpdated(val location: MapLocationMarker?) : Label
        data class ViewportCommandRequested(val command: MapViewportCommand) : Label
        data class LocationRequestIssued(val request: MapLocationRequest) : Label
    }
}
