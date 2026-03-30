package ru.tech.demomapapp.feature.map.render

internal fun RenderMapStyle.styleUrl(): String =
    when (this) {
        RenderMapStyle.DEFAULT -> "https://tiles.openfreemap.org/styles/liberty"
    }