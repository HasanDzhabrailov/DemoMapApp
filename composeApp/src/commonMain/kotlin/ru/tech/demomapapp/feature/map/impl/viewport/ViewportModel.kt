package ru.tech.demomapapp.feature.map.impl.viewport

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal data class ViewportModel(
    val cameraSnapshot: MapCameraSnapshot? = null,
    val pendingCommand: MapViewportCommand? = null,
    val isCenterMarkerMenuVisible: Boolean = false,
)
