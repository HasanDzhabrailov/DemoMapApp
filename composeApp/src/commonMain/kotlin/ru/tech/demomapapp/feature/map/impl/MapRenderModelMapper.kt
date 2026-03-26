package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.render.MapRenderModel
import ru.tech.demomapapp.feature.map.render.RenderMapPoint
import ru.tech.demomapapp.feature.map.render.RenderMapStyle

fun MapState.toRenderModel(): MapRenderModel =
    MapRenderModel(
        style = style.toRenderStyle(),
        points = points.map { point ->
            RenderMapPoint(
                latitude = point.latitude,
                longitude = point.longitude,
                label = point.title,
            )
        },
    )

private fun MapStyle.toRenderStyle(): RenderMapStyle =
    when (this) {
        MapStyle.DEMO -> RenderMapStyle.DEFAULT
    }
