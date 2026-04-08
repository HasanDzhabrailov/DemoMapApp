package ru.tech.demomapapp.feature.map.render

internal fun RenderMapStyle.styleUrl(): String = when (this) {
    RenderMapStyle.DEFAULT -> "https://demotiles.maplibre.org/style.json"
    RenderMapStyle.OPEN_STREET_MAP -> "https://tiles.openfreemap.org/styles/liberty"
}
