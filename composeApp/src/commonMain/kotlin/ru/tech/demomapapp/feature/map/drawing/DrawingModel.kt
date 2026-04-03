package ru.tech.demomapapp.feature.map.drawing

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapVertex

enum class DrawingMode {
    LINE,
    POLYGON,
}

data class CreatePointDraft(
    val latitudeInput: String,
    val longitudeInput: String,
    val titleInput: String = "",
)

data class ShapeDrawingDraft(
    val mode: DrawingMode,
    val fixedVertices: List<MapVertex> = emptyList(),
    val titleInput: String = "",
)

data class DrawingModel(
    val points: List<MapPoint> = emptyList(),
    val lines: List<MapLine> = emptyList(),
    val polygons: List<MapPolygon> = emptyList(),
    val isCreatePointSheetVisible: Boolean = false,
    val createPointDraft: CreatePointDraft? = null,
    val drawingMode: DrawingMode? = null,
    val shapeDrawingDraft: ShapeDrawingDraft? = null,
    val isCreateShapeSheetVisible: Boolean = false,
    val lastCameraSnapshot: MapCameraSnapshot? = null,
)

internal fun MapCameraSnapshot.toVertex(): MapVertex = MapVertex(
    latitude = latitude,
    longitude = longitude,
)
