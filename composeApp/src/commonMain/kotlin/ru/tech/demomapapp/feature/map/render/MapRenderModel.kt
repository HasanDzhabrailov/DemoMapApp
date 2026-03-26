package ru.tech.demomapapp.feature.map.render

data class MapRenderModel(
    val style: RenderMapStyle = RenderMapStyle.DEFAULT,
    val points: List<RenderMapPoint> = emptyList(),
)

data class RenderMapPoint(
    val key: String,
    val latitude: Double,
    val longitude: Double,
    val label: String,
)

data class RenderPointClick(
    val pointKey: String,
    val anchor: RenderPointAnchor,
)

data class RenderPointAnchor(
    val screenX: Int,
    val screenY: Int,
)

enum class RenderMapStyle {
    DEFAULT,
}
