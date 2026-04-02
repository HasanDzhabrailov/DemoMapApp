package ru.tech.demomapapp.feature.map.impl.ruler

import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

data class RulerModel(
    val isEnabled: Boolean = false,
    val measurement: RulerMeasurement? = null,
    val infoWindow: RulerInfoWindowState? = null,
)
