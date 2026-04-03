package ru.tech.demomapapp.feature.map.location

import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

interface LocationComponent {
    val model: Value<LocationModel>

    fun onGpsToggle()
    fun onMyLocationClick()
    fun onCurrentLocationFocusClick()
    fun onLocationResult(result: LocationRequestResult)
    fun onLocationRequestConsumed()
    fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot)

    interface Output {
        fun onStateChanged()
        fun onLocationUpdated(location: MapLocationMarker?)
        fun onViewportCommandRequested(command: MapViewportCommand)
        fun onLocationRequestIssued(request: MapLocationRequest)
    }
}
