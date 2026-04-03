package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon

interface DrawingComponent {
    val model: Value<DrawingModel>
    val pointSheetSlot: Value<ChildSlot<*, PointSheetChild>>
    val shapeSheetSlot: Value<ChildSlot<*, ShapeSheetChild>>

    fun onCreatePointClick()
    fun onCreateLineClick()
    fun onCreatePolygonClick()
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
    fun onCameraPositionUpdated(snapshot: MapCameraSnapshot)

    sealed interface PointSheetChild {
        data object Content : PointSheetChild
    }

    sealed interface ShapeSheetChild {
        data object Content : ShapeSheetChild
    }

    interface Output {
        fun onStateChanged()
        fun onFeatureCreated(feature: CreatedFeature)
    }

    sealed interface CreatedFeature {
        data class Point(val point: MapPoint) : CreatedFeature
        data class Line(val line: MapLine) : CreatedFeature
        data class Polygon(val polygon: MapPolygon) : CreatedFeature
    }
}
