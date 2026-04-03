package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon

internal interface DrawingStore : Store<DrawingStore.Intent, DrawingStore.State, DrawingStore.Label> {
    sealed interface Intent {
        object CreatePointClicked : Intent
        data class PointLatitudeChanged(val value: String) : Intent
        data class PointLongitudeChanged(val value: String) : Intent
        data class PointTitleChanged(val value: String) : Intent
        object PointConfirmed : Intent
        object PointSheetDismissed : Intent

        object CreateLineClicked : Intent
        object CreatePolygonClicked : Intent
        object DrawingAddPositionClicked : Intent
        object DrawingRemoveLastPositionClicked : Intent
        object DrawingDetailsClicked : Intent
        data class ShapeTitleChanged(val value: String) : Intent
        object ShapeConfirmed : Intent
        object ShapeSheetDismissed : Intent
        object DrawingDismissed : Intent

        data class CameraPositionUpdated(val snapshot: MapCameraSnapshot) : Intent
    }

    data class State(
        val points: List<MapPoint> = emptyList(),
        val lines: List<MapLine> = emptyList(),
        val polygons: List<MapPolygon> = emptyList(),
        val isCreatePointSheetVisible: Boolean = false,
        val createPointDraft: CreatePointDraft? = null,
        val drawingMode: DrawingMode? = null,
        val shapeDrawingDraft: ShapeDrawingDraft? = null,
        val isCreateShapeSheetVisible: Boolean = false,
        val lastCameraSnapshot: MapCameraSnapshot? = null,
    ) {
        fun toModel(): DrawingModel = DrawingModel(
            points = points,
            lines = lines,
            polygons = polygons,
            isCreatePointSheetVisible = isCreatePointSheetVisible,
            createPointDraft = createPointDraft,
            drawingMode = drawingMode,
            shapeDrawingDraft = shapeDrawingDraft,
            isCreateShapeSheetVisible = isCreateShapeSheetVisible,
            lastCameraSnapshot = lastCameraSnapshot,
        )

        companion object {
            fun fromModel(model: DrawingModel): State = State(
                points = model.points,
                lines = model.lines,
                polygons = model.polygons,
                isCreatePointSheetVisible = model.isCreatePointSheetVisible,
                createPointDraft = model.createPointDraft,
                drawingMode = model.drawingMode,
                shapeDrawingDraft = model.shapeDrawingDraft,
                isCreateShapeSheetVisible = model.isCreateShapeSheetVisible,
                lastCameraSnapshot = model.lastCameraSnapshot,
            )
        }
    }

    sealed interface Message {
        object CreatePointSheetOpened : Message
        data class PointLatitudeUpdated(val value: String) : Message
        data class PointLongitudeUpdated(val value: String) : Message
        data class PointTitleUpdated(val value: String) : Message
        data class PointCreated(val point: MapPoint) : Message
        object CreatePointSheetClosed : Message
        data class CreatePointDraftInitialized(val draft: CreatePointDraft) : Message

        data class DrawingModeEntered(val mode: DrawingMode) : Message
        data class DrawingPositionAdded(val snapshot: MapCameraSnapshot) : Message
        object DrawingLastPositionRemoved : Message
        data class ShapeTitleUpdated(val value: String) : Message
        object ShapeSheetOpened : Message
        data class LineCreated(val line: MapLine) : Message
        data class PolygonCreated(val polygon: MapPolygon) : Message
        object ShapeSheetClosed : Message
        object DrawingModeExited : Message
        data class CameraPositionUpdated(val snapshot: MapCameraSnapshot) : Message
    }

    sealed interface Label {
        sealed interface FeatureCreated : Label {
            data class Point(val point: ru.tech.demomapapp.feature.map.api.MapPoint) : FeatureCreated
            data class Line(val line: ru.tech.demomapapp.feature.map.api.MapLine) : FeatureCreated
            data class Polygon(val polygon: ru.tech.demomapapp.feature.map.api.MapPolygon) : FeatureCreated
        }
    }
}
