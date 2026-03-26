package ru.tech.demomapapp.feature.map.render

data class MapRenderModel(
    val style: RenderMapStyle = RenderMapStyle.DEFAULT,
    val points: List<RenderMapPoint> = emptyList(),
)

data class RenderMapPoint(
    val latitude: Double,
    val longitude: Double,
    val label: String,
)

enum class RenderMapStyle {
    DEFAULT,
}
