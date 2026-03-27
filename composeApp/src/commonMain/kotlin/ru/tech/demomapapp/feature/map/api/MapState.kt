package ru.tech.demomapapp.feature.map.api

data class MapState(
    val style: MapStyle = MapStyle.DEMO,
    val points: List<MapPoint> = emptyList(),
    val lines: List<MapLine> = emptyList(),
    val polygons: List<MapPolygon> = emptyList(),
)
