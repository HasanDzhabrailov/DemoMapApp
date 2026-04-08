package ru.tech.demomapapp.feature.map.api

data class MapPolygon(
    val id: String,
    val vertices: List<MapVertex>,
    val title: String,
    val createdAtEpochMillis: Long,
)
