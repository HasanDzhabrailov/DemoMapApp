package ru.tech.demomapapp.feature.map.api

data class MapPoint(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val createdAtEpochMillis: Long,
)
