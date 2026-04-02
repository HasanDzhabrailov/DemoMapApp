package ru.tech.demomapapp.feature.map.impl.location

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MyLocationMode

internal object LocationReducer : Reducer<LocationStore.State, LocationStore.Message> {
    override fun LocationStore.State.reduce(msg: LocationStore.Message): LocationStore.State = when (msg) {
        is LocationStore.Message.CameraSnapshotStored -> copy(lastCameraSnapshot = msg.snapshot)
        LocationStore.Message.GpsEnableRequested -> copy(
            mode = MyLocationMode.OFF,
            currentMarker = null,
            pendingRequest = MapLocationRequest.EnableGpsLocationRequest,
            hasRealLocation = false,
        )
        LocationStore.Message.GpsDisabled -> copy(
            mode = MyLocationMode.OFF,
            currentMarker = null,
            pendingRequest = null,
            hasRealLocation = false,
        )
        is LocationStore.Message.ManualPlaceholderSelected -> copy(
            mode = MyLocationMode.MANUAL_PLACEHOLDER,
            currentMarker = msg.marker,
            pendingRequest = null,
            hasRealLocation = false,
        )
        LocationStore.Message.RecenterRequestIssued -> copy(
            pendingRequest = MapLocationRequest.RecenterToGpsLocationRequest,
        )
        LocationStore.Message.PendingRequestCleared -> copy(pendingRequest = null)
        is LocationStore.Message.LocationResolved -> copy(
            mode = MyLocationMode.GPS,
            currentMarker = msg.marker,
            pendingRequest = null,
            hasRealLocation = true,
        )
        LocationStore.Message.LocationCleared -> copy(
            mode = MyLocationMode.OFF,
            currentMarker = null,
            pendingRequest = null,
            hasRealLocation = false,
        )
    }
}
