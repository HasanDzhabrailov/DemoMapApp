package ru.tech.demomapapp.feature.map.api

data class MapCameraSnapshot(
    val latitude: Double,
    val longitude: Double,
    val zoom: Double,
    val bearing: Double,
)
