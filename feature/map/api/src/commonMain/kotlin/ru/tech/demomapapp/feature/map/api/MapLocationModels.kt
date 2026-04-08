package ru.tech.demomapapp.feature.map.api

enum class MyLocationMode {
    OFF,
    MANUAL_PLACEHOLDER,
    GPS,
}

data class MapLocationMarker(
    val latitude: Double,
    val longitude: Double,
    val isPlaceholder: Boolean,
)

sealed interface MapLocationRequest {
    data object EnableGpsLocationRequest : MapLocationRequest

    data object RecenterToGpsLocationRequest : MapLocationRequest
}

sealed interface LocationRequestResult {
    data object PermissionDenied : LocationRequestResult

    data object LocationUnavailable : LocationRequestResult

    data class LocationResolved(
        val latitude: Double,
        val longitude: Double,
    ) : LocationRequestResult
}
