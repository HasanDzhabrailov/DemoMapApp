package ru.tech.demomapapp.feature.map.api

sealed interface MapViewportCommand {
    data object ZoomIn : MapViewportCommand

    data object ZoomOut : MapViewportCommand

    data class MoveTo(
        val latitude: Double,
        val longitude: Double,
    ) : MapViewportCommand
}
