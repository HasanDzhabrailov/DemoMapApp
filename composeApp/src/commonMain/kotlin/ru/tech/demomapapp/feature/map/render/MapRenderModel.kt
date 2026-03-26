package ru.tech.demomapapp.feature.map.render

data class MapRenderModel(
    val style: RenderMapStyle = RenderMapStyle.DEFAULT,
)

enum class RenderMapStyle {
    DEFAULT,
}
