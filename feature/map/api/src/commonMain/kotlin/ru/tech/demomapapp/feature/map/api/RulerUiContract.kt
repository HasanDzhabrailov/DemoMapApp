package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

/**
 * UI contract for ruler feature.
 * Minimal interface exposing only what the UI needs.
 */
interface RulerUiContract {
    val model: Value<RulerModel>
}

/**
 * Model for ruler UI state.
 * Defined in API to avoid internal imports.
 */
data class RulerModel(
    val isEnabled: Boolean = false,
    val currentLocation: MapLocationMarker? = null,
    val lastCameraSnapshot: MapCameraSnapshot? = null,
    val measurement: RulerMeasurement? = null,
    val infoWindow: RulerInfoWindowState? = null,
)
