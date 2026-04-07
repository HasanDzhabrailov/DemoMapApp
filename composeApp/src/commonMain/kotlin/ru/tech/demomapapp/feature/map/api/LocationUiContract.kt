package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

/**
 * UI contract for location controls.
 * Minimal interface exposing only what the UI needs.
 */
interface LocationUiContract {
    val model: Value<LocationModel>

    fun onGpsToggle()
    fun onMyLocationClick()
    fun onCurrentLocationFocusClick()
    fun onLocationResult(result: LocationRequestResult)
    fun onLocationRequestConsumed()
}

/**
 * Model for location UI state.
 * Defined in API to avoid internal imports.
 */
data class LocationModel(
    val mode: MyLocationMode = MyLocationMode.OFF,
    val currentMarker: MapLocationMarker? = null,
    val pendingRequest: MapLocationRequest? = null,
)