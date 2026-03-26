package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.render.MapRenderModel
import ru.tech.demomapapp.feature.map.render.RenderMapStyle

fun MapState.toRenderModel(): MapRenderModel =
    MapRenderModel(
        style = style.toRenderStyle(),
    )

private fun MapStyle.toRenderStyle(): RenderMapStyle =
    when (this) {
        MapStyle.DEMO -> RenderMapStyle.DEFAULT
    }
