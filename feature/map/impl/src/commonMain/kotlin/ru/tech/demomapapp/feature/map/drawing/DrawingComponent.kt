package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.DrawingUiContract
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon

/**
 * DrawingComponent extends DrawingUiContract to expose minimal UI interface.
 * Internal methods (onCreatePointClick, onCreateLineClick, etc.) remain in this interface.
 */
interface DrawingComponent : DrawingUiContract {
    override val model: Value<DrawingModel>
    override val pointSheetSlot: Value<ChildSlot<*, PointSheetChild>>
    override val shapeSheetSlot: Value<ChildSlot<*, ShapeSheetChild>>

    fun onCreatePointClick()
    fun onCreateLineClick()
    fun onCreatePolygonClick()
    override fun onCreatePointLatitudeChange(value: String)
    override fun onCreatePointLongitudeChange(value: String)
    override fun onCreatePointTitleChange(value: String)
    override fun onCreatePointConfirm()
    override fun onCreatePointSheetDismiss()
    override fun onDrawingAddPositionClick()
    override fun onDrawingRemoveLastPositionClick()
    override fun onDrawingDetailsClick()
    override fun onDrawingDismiss()
    override fun onCreateShapeTitleChange(value: String)
    override fun onCreateShapeConfirm()
    override fun onCreateShapeSheetDismiss()
    fun onCameraPositionUpdated(snapshot: MapCameraSnapshot)

    sealed interface PointSheetChild : DrawingUiContract.PointSheetChild {
        data object Content : PointSheetChild
    }

    sealed interface ShapeSheetChild : DrawingUiContract.ShapeSheetChild {
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
