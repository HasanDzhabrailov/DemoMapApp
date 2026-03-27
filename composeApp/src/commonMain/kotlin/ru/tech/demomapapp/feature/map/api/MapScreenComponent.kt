package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>

    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onMapToolsClick()
    fun onMapToolsDismiss()
    fun onZoomInClick()
    fun onZoomOutClick()
    fun onAvailableMapsClick()
    fun onMapsOnScreenClick()
    fun onGpsToggle()
    fun onMyLocationClick()
    fun onCurrentLocationFocusClick()
    fun onLocationRequestConsumed()
    fun onLocationResult(result: LocationRequestResult)
    fun onRulerToggle()
    fun onViewportCommandConsumed()
    fun onCenterMarkerClick()
    fun onCenterMarkerMenuDismiss()
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
    fun onFeatureClick(
        featureKey: String,
        featureType: FeatureType,
        anchor: FeatureInfoWindowAnchor,
    )
    fun onFeatureInfoWindowDismiss()

    data class Model(
        val mapState: MapState = MapState(),
        val lastCameraSnapshot: MapCameraSnapshot? = null,
        val isMapToolsMenuVisible: Boolean = false,
        val myLocationMode: MyLocationMode = MyLocationMode.OFF,
        val currentLocationMarker: MapLocationMarker? = null,
        val pendingLocationRequest: MapLocationRequest? = null,
        val isRulerEnabled: Boolean = false,
        val rulerMeasurement: RulerMeasurement? = null,
        val rulerInfoWindow: RulerInfoWindowState? = null,
        val pendingViewportCommand: MapViewportCommand? = null,
        val isCenterMarkerMenuVisible: Boolean = false,
        val isCreatePointSheetVisible: Boolean = false,
        val createPointDraft: CreatePointDraft? = null,
        val drawingMode: DrawingMode? = null,
        val shapeDrawingDraft: ShapeDrawingDraft? = null,
        val isCreateShapeSheetVisible: Boolean = false,
        val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    )

    enum class DrawingMode {
        LINE,
        POLYGON,
    }

    enum class FeatureType {
        POINT,
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

    data class FeatureInfoWindow(
        val title: String,
        val createdAtText: String,
        val anchor: FeatureInfoWindowAnchor,
    )

    data class FeatureInfoWindowAnchor(
        val screenX: Int,
        val screenY: Int,
    )
}
