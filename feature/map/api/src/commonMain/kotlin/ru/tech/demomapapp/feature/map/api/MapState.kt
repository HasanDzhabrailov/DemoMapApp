package ru.tech.demomapapp.feature.map.api

data class MapState(
    val style: MapStyle = MapStyle.DEMO,
    val overlayLayers: List<MapLayerEntry> = emptyList(),
    val points: List<MapPoint> = emptyList(),
    val lines: List<MapLine> = emptyList(),
    val polygons: List<MapPolygon> = emptyList(),
)
