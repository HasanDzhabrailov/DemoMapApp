package ru.tech.demomapapp.feature.map.api

data class MapLine(
    val id: String,
    val vertices: List<MapVertex>,
    val title: String,
    val createdAtEpochMillis: Long,
)
