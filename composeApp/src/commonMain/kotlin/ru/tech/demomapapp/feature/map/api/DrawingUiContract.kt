package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

/**
 * UI contract for drawing features.
 * Minimal interface exposing only what the UI needs.
 */
interface DrawingUiContract {
    val model: Value<DrawingModel>
    val pointSheetSlot: Value<ChildSlot<*, PointSheetChild>>
    val shapeSheetSlot: Value<ChildSlot<*, ShapeSheetChild>>

    fun onCreatePointLatitudeChange(value: String)
    fun onCreatePointLongitudeChange(value: String)
    fun onCreatePointTitleChange(value: String)
    fun onCreatePointConfirm()
    fun onCreatePointSheetDismiss()
    fun onDrawingAddPositionClick()
    fun onDrawingRemoveLastPositionClick()
    fun onDrawingDetailsClick()
    fun onDrawingDismiss()
    fun onCreateShapeTitleChange(value: String)
    fun onCreateShapeConfirm()
    fun onCreateShapeSheetDismiss()

    interface PointSheetChild {
        data object Content : PointSheetChild
    }

    interface ShapeSheetChild {
        data object Content : ShapeSheetChild
    }
}

/**
 * Model for drawing UI state.
 * Defined in API to avoid internal imports.
 */
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

enum class DrawingMode {
    LINE,
    POLYGON,
}