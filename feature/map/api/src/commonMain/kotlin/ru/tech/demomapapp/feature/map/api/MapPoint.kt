package ru.tech.demomapapp.feature.map.api

data class MapPoint(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val createdAtEpochMillis: Long,
)
