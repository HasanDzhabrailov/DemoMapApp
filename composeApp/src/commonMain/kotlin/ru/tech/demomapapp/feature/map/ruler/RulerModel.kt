package ru.tech.demomapapp.feature.map.ruler

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

data class RulerModel(
    val isEnabled: Boolean = false,
    val currentLocation: MapLocationMarker? = null,
    val lastCameraSnapshot: MapCameraSnapshot? = null,
    val measurement: RulerMeasurement? = null,
    val infoWindow: RulerInfoWindowState? = null,
)
