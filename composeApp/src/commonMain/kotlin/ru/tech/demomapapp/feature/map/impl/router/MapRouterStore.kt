package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapCatalogItem
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal interface MapRouterStore : Store<MapRouterStore.Intent, MapRouterStore.State, MapRouterStore.Label> {

    sealed interface Intent {
        data class ViewportStateUpdated(val state: ChildState.Viewport) : Intent
        data class ToolsStateUpdated(val state: ChildState.Tools) : Intent
        data class LocationStateUpdated(val state: ChildState.Location) : Intent
        data class RulerStateUpdated(val state: ChildState.Ruler) : Intent
        data class CenterMarkerStateUpdated(val state: ChildState.CenterMarker) : Intent
        data class CreatePointStateUpdated(val state: ChildState.CreatePoint) : Intent
        data class DrawingStateUpdated(val state: ChildState.Drawing) : Intent
        data class FeatureSelectionStateUpdated(val state: ChildState.FeatureSelection) : Intent
    }

    data class State(
        val viewportState: ChildState.Viewport? = null,
        val toolsState: ChildState.Tools? = null,
        val locationState: ChildState.Location? = null,
        val rulerState: ChildState.Ruler? = null,
        val centerMarkerState: ChildState.CenterMarker? = null,
        val createPointState: ChildState.CreatePoint? = null,
        val drawingState: ChildState.Drawing? = null,
        val featureSelectionState: ChildState.FeatureSelection? = null,
    ) {
        val mapState: MapState
            get() = MapState(
                style = toolsState?.selectedMapStyle ?: MapStyle.DEMO,
                overlayLayers = toolsState?.overlayLayers ?: emptyList(),
                points = featureSelectionState?.points ?: emptyList(),
                lines = featureSelectionState?.lines ?: emptyList(),
                polygons = featureSelectionState?.polygons ?: emptyList(),
            )

        val lastCameraSnapshot: MapCameraSnapshot?
            get() = viewportState?.lastCameraSnapshot

        val isMapToolsMenuVisible: Boolean
            get() = toolsState?.isMapToolsMenuVisible ?: false

        val isAvailableMapsSheetVisible: Boolean
            get() = toolsState?.isAvailableMapsSheetVisible ?: false

        val selectedAvailableMap: MapCatalogItem?
            get() = toolsState?.selectedAvailableMap

        val isMapsOnScreenSheetVisible: Boolean
            get() = toolsState?.isMapsOnScreenSheetVisible ?: false

        val selectedOverlayLayer: MapLayerEntry?
            get() = toolsState?.selectedOverlayLayer

        val editingOverlayOpacityLayer: MapLayerEntry?
            get() = toolsState?.editingOverlayOpacityLayer

        val myLocationMode: MyLocationMode
            get() = locationState?.myLocationMode ?: MyLocationMode.OFF

        val currentLocationMarker: MapLocationMarker?
            get() = locationState?.currentLocationMarker

        val activeLocationRequest: MapLocationRequest?
            get() = locationState?.activeLocationRequest

        val isRulerEnabled: Boolean
            get() = rulerState?.isRulerEnabled ?: false

        val rulerMeasurement: RulerMeasurement?
            get() = rulerState?.rulerMeasurement

        val rulerInfoWindow: RulerInfoWindowState?
            get() = rulerState?.rulerInfoWindow

        val isCenterMarkerMenuVisible: Boolean
            get() = centerMarkerState?.isMenuVisible ?: false

        val isCreatePointSheetVisible: Boolean
            get() = createPointState?.isSheetVisible ?: false

        val createPointDraft: CreatePointDraft?
            get() = createPointState?.draft

        val drawingMode: DrawingMode?
            get() = drawingState?.mode

        val shapeDrawingDraft: ShapeDrawingDraft?
            get() = drawingState?.shapeDraft

        val isCreateShapeSheetVisible: Boolean
            get() = drawingState?.isCreateShapeSheetVisible ?: false

        val selectedFeatureInfoWindow: FeatureInfoWindow?
            get() = featureSelectionState?.selectedInfoWindow
    }

    sealed interface Label {
        data class ViewportCommandRequested(val command: MapViewportCommand) : Label
        data class LocationRequestIssued(val request: MapLocationRequest) : Label
    }

    sealed interface ChildState {
        data class Viewport(
            val lastCameraSnapshot: MapCameraSnapshot? = null,
        ) : ChildState

        data class Tools(
            val selectedMapStyle: MapStyle = MapStyle.DEMO,
            val overlayLayers: List<MapLayerEntry> = emptyList(),
            val isMapToolsMenuVisible: Boolean = false,
            val isAvailableMapsSheetVisible: Boolean = false,
            val selectedAvailableMap: MapCatalogItem? = null,
            val isMapsOnScreenSheetVisible: Boolean = false,
            val selectedOverlayLayer: MapLayerEntry? = null,
            val editingOverlayOpacityLayer: MapLayerEntry? = null,
        ) : ChildState

        data class Location(
            val myLocationMode: MyLocationMode = MyLocationMode.OFF,
            val currentLocationMarker: MapLocationMarker? = null,
            val activeLocationRequest: MapLocationRequest? = null,
        ) : ChildState

        data class Ruler(
            val isRulerEnabled: Boolean = false,
            val rulerMeasurement: RulerMeasurement? = null,
            val rulerInfoWindow: RulerInfoWindowState? = null,
        ) : ChildState

        data class CenterMarker(
            val isMenuVisible: Boolean = false,
        ) : ChildState

        data class CreatePoint(
            val isSheetVisible: Boolean = false,
            val draft: CreatePointDraft? = null,
        ) : ChildState

        data class Drawing(
            val mode: DrawingMode? = null,
            val shapeDraft: ShapeDrawingDraft? = null,
            val isCreateShapeSheetVisible: Boolean = false,
        ) : ChildState

        data class FeatureSelection(
            val points: List<MapPoint> = emptyList(),
            val lines: List<MapLine> = emptyList(),
            val polygons: List<MapPolygon> = emptyList(),
            val selectedInfoWindow: FeatureInfoWindow? = null,
        ) : ChildState
    }

    data class CreatePointDraft(
        val latitudeInput: String,
        val longitudeInput: String,
        val titleInput: String = "",
    )

    enum class DrawingMode {
        LINE,
        POLYGON,
    }

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
