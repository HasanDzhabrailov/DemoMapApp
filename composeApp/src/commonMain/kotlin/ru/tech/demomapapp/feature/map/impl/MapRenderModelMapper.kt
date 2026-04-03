package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.api.RulerMeasurement
import ru.tech.demomapapp.feature.map.render.MapRenderModel
import ru.tech.demomapapp.feature.map.render.RenderCurrentLocationMarker
import ru.tech.demomapapp.feature.map.render.RenderMapLine
import ru.tech.demomapapp.feature.map.render.RenderMapPoint
import ru.tech.demomapapp.feature.map.render.RenderMapPolygon
import ru.tech.demomapapp.feature.map.render.RenderMapStyle
import ru.tech.demomapapp.feature.map.render.RenderMapVertex
import ru.tech.demomapapp.feature.map.render.RenderRasterTileLayer
import ru.tech.demomapapp.feature.map.render.RenderRulerMeasurement

internal fun MapState.toRenderModel(
    shapeDrawingDraft: MapScreenComponent.ShapeDrawingDraft? = null,
    currentSnapshot: MapCameraSnapshot? = null,
    currentLocationMarker: MapLocationMarker? = null,
    rulerMeasurement: RulerMeasurement? = null,
    shapeDrawingPreviewMapper: ShapeDrawingPreviewMapper = DefaultShapeDrawingPreviewMapper,
    rulerArrowGeometryCalculator: RulerArrowGeometryCalculator = DefaultRulerArrowGeometryCalculator,
): MapRenderModel = MapRenderModel(
    style = style.toRenderStyle(),
    tileLayers = overlayLayers.map(MapLayerEntry::toRenderTileLayer),
    points = points.map(MapPoint::toRenderPoint),
    lines = lines.map(MapLine::toRenderLine),
    polygons = polygons.map(MapPolygon::toRenderPolygon),
    currentLocationMarker = currentLocationMarker?.toRenderCurrentLocationMarker(),
    rulerMeasurement = rulerMeasurement?.toRenderRulerMeasurement(rulerArrowGeometryCalculator),
    drawingPreview = shapeDrawingPreviewMapper.map(
        draft = shapeDrawingDraft,
        currentSnapshot = currentSnapshot,
    ),
)

private fun MapStyle.toRenderStyle(): RenderMapStyle = when (this) {
    MapStyle.DEMO -> RenderMapStyle.DEFAULT
    MapStyle.OPEN_STREET_MAP -> RenderMapStyle.OPEN_STREET_MAP
}

private fun MapLayerEntry.toRenderTileLayer(): RenderRasterTileLayer = RenderRasterTileLayer(
    key = id,
    title = title,
    templateId = source.templateId,
    tileSize = source.tileSize,
    minZoom = source.minZoom,
    maxZoom = source.maxZoom,
    opacity = opacity,
)

private fun MapPoint.toRenderPoint(): RenderMapPoint = RenderMapPoint(
    key = id,
    latitude = latitude,
    longitude = longitude,
    label = title,
)

private fun MapLocationMarker.toRenderCurrentLocationMarker(): RenderCurrentLocationMarker =
    RenderCurrentLocationMarker(
        latitude = latitude,
        longitude = longitude,
        isPlaceholder = isPlaceholder,
    )

private fun RulerMeasurement.toRenderRulerMeasurement(
    rulerArrowGeometryCalculator: RulerArrowGeometryCalculator,
): RenderRulerMeasurement = RenderRulerMeasurement(
    startLatitude = startLatitude,
    startLongitude = startLongitude,
    endLatitude = endLatitude,
    endLongitude = endLongitude,
    arrowSegments = rulerArrowGeometryCalculator.calculate(this),
)

private fun MapLine.toRenderLine(): RenderMapLine {
    val labelVertex = vertices.labelVertex()
    return RenderMapLine(
        key = id,
        vertices = vertices.map(MapVertex::toRenderVertex),
        label = title,
        labelLatitude = labelVertex.latitude,
        labelLongitude = labelVertex.longitude,
    )
}

private fun MapPolygon.toRenderPolygon(): RenderMapPolygon {
    val labelVertex = vertices.labelVertex()
    return RenderMapPolygon(
        key = id,
        vertices = vertices.map(MapVertex::toRenderVertex),
        label = title,
        labelLatitude = labelVertex.latitude,
        labelLongitude = labelVertex.longitude,
    )
}

private fun List<MapVertex>.labelVertex(): MapVertex {
    val latitude = map(MapVertex::latitude).average()
    val longitude = map(MapVertex::longitude).average()
    return MapVertex(latitude = latitude, longitude = longitude)
}

private fun MapVertex.toRenderVertex(): RenderMapVertex = RenderMapVertex(
    latitude = latitude,
    longitude = longitude,
)
