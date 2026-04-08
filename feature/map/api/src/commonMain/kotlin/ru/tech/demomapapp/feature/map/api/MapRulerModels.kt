package ru.tech.demomapapp.feature.map.api

data class RulerMeasurement(
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val distanceMeters: Double,
    val trueAzimuthDegrees: Double,
)

data class RulerInfoWindowState(
    val distanceText: String,
    val trueAzimuthText: String,
    val magneticAzimuthText: String? = null,
    val directionalAngleText: String? = null,
)
