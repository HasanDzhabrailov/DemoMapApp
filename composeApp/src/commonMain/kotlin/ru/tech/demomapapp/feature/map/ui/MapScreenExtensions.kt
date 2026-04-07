package ru.tech.demomapapp.feature.map.ui

import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.render.RenderFeatureClick
import ru.tech.demomapapp.feature.map.render.RenderFeatureType

internal fun RenderFeatureClick.toFeatureInfoWindowAnchor(): MapScreenComponent.FeatureInfoWindowAnchor =
    MapScreenComponent.FeatureInfoWindowAnchor(
        screenX = anchor.screenX,
        screenY = anchor.screenY,
    )

internal fun RenderFeatureClick.toFeatureType(): MapScreenComponent.FeatureType = when (featureType) {
    RenderFeatureType.POINT -> MapScreenComponent.FeatureType.POINT
    RenderFeatureType.LINE -> MapScreenComponent.FeatureType.LINE
    RenderFeatureType.POLYGON -> MapScreenComponent.FeatureType.POLYGON
}