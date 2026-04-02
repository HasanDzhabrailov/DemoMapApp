package ru.tech.demomapapp.feature.map.impl.drawing

import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon

interface DrawingComponent {
    val model: Value<DrawingModel>

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
    fun onDrawingDismiss()
    fun onCreateShapeTitleChange(value: String)
    fun onCreateShapeConfirm()
    fun onCreateShapeSheetDismiss()
    fun onCameraPositionUpdated(snapshot: MapCameraSnapshot)

    fun interface Output {
        fun onFeatureCreated(feature: CreatedFeature)
    }

    sealed interface CreatedFeature {
        data class Point(val point: MapPoint) : CreatedFeature
        data class Line(val line: MapLine) : CreatedFeature
        data class Polygon(val polygon: MapPolygon) : CreatedFeature
    }
}
