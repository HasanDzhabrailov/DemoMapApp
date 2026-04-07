package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>

    data class Model(
        val mapState: MapState = MapState(),
        val availableMapCatalog: List<MapCatalogItem> = MapLayerCatalog.items(),
        val lastCameraSnapshot: MapCameraSnapshot? = null,
        val isMapToolsMenuVisible: Boolean = false,
        val isAvailableMapsSheetVisible: Boolean = false,
        val selectedAvailableMap: MapCatalogItem? = null,
        val isMapsOnScreenSheetVisible: Boolean = false,
        val selectedOverlayLayer: MapLayerEntry? = null,
        val editingOverlayOpacityLayer: MapLayerEntry? = null,
        val myLocationMode: MyLocationMode = MyLocationMode.OFF,
        val currentLocationMarker: MapLocationMarker? = null,
        val pendingLocationRequest: MapLocationRequest? = null,
        val isRulerEnabled: Boolean = false,
        val rulerMeasurement: RulerMeasurement? = null,
        val rulerInfoWindow: RulerInfoWindowState? = null,
        val pendingViewportCommand: MapViewportCommand? = null,
        val isCenterMarkerMenuVisible: Boolean = false,
        val isCenterMarkerEnabled: Boolean = true,
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
