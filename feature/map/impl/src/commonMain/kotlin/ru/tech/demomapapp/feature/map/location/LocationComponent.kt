package ru.tech.demomapapp.feature.map.location

import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.LocationUiContract
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

/**
 * LocationComponent extends LocationUiContract to expose minimal UI interface.
 * Internal methods (onCameraSnapshotReceived) remain in this interface only.
 */
interface LocationComponent : LocationUiContract {
    override val model: Value<LocationModel>

    override fun onGpsToggle()
    override fun onMyLocationClick()
    override fun onCurrentLocationFocusClick()
    override fun onLocationResult(result: LocationRequestResult)
    override fun onLocationRequestConsumed()
    fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot)

    interface Output {
        fun onStateChanged()
        fun onLocationUpdated(location: MapLocationMarker?)
        fun onViewportCommandRequested(command: MapViewportCommand)
        fun onLocationRequestIssued(request: MapLocationRequest)
    }
}
