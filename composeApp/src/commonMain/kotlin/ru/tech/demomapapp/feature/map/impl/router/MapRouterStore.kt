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
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal interface MapRouterStore : Store<MapRouterStore.Intent, MapRouterStore.State, MapRouterStore.Label> {

    sealed interface Intent {
        // Child state updates - these will be triggered by external state observation
        data class ViewportStateUpdated(val state: ChildState.Viewport) : Intent
        data class ToolsStateUpdated(val state: ChildState.Tools) : Intent
        data class LocationStateUpdated(val state: ChildState.Location) : Intent
        data class RulerStateUpdated(val state: ChildState.Ruler) : Intent
        data class CenterMarkerStateUpdated(val state: ChildState.CenterMarker) : Intent
        data class DrawingStateUpdated(val state: ChildState.Drawing) : Intent

        // Overlay management
        data class OverlayInteractionRequested(val target: OverlayTarget) : Intent
        data object ToolsMenuDismissRequested : Intent
        data object ViewportMenuDismissRequested : Intent

        // Feature selection
        data class FeatureClicked(
            val featureKey: String,
            val featureType: MapScreenComponent.FeatureType,
            val anchor: MapScreenComponent.FeatureInfoWindowAnchor,
        ) : Intent
        data object FeatureInfoWindowDismissed : Intent

        // Center marker actions
        data object CenterMarkerClicked : Intent

        // Viewport commands
        data class ViewportCommandUpdated(
            val source: ViewportCommandSource,
            val command: MapViewportCommand?,
        ) : Intent
        data object ViewportCommandConsumed : Intent
    }

    data class State(
        val viewportState: ChildState.Viewport? = null,
        val toolsState: ChildState.Tools? = null,
        val locationState: ChildState.Location? = null,
        val rulerState: ChildState.Ruler? = null,
        val centerMarkerState: ChildState.CenterMarker? = null,
        val drawingState: ChildState.Drawing? = null,
        val selectedFeatureInfoWindow: MapScreenComponent.FeatureInfoWindow? = null,
        val viewportPendingCommand: MapViewportCommand? = null,
        val locationPendingViewportCommand: MapViewportCommand? = null,
        val rulerPendingViewportCommand: MapViewportCommand? = null,
    ) {
        // Business rule: center marker is disabled during drawing mode
        val isCenterMarkerEnabled: Boolean
            get() = drawingState?.mode == null && drawingState?.isCreatePointSheetVisible != true
        val mapState: MapState
            get() = MapState(
                style = toolsState?.selectedMapStyle ?: MapStyle.DEMO,
                overlayLayers = toolsState?.overlayLayers ?: emptyList(),
                points = drawingState?.points ?: emptyList(),
                lines = drawingState?.lines ?: emptyList(),
                polygons = drawingState?.polygons ?: emptyList(),
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
            get() = drawingState?.isCreatePointSheetVisible ?: false

        val createPointDraft: CreatePointDraft?
            get() = drawingState?.createPointDraft

        val drawingMode: DrawingMode?
            get() = drawingState?.mode

        val shapeDrawingDraft: ShapeDrawingDraft?
            get() = drawingState?.shapeDraft

        val isCreateShapeSheetVisible: Boolean
            get() = drawingState?.isCreateShapeSheetVisible ?: false

        val pendingViewportCommand: MapViewportCommand?
            get() = viewportPendingCommand ?: locationPendingViewportCommand ?: rulerPendingViewportCommand

        val currentViewportCommandSource: ViewportCommandSource?
            get() = when {
                viewportPendingCommand != null -> ViewportCommandSource.VIEWPORT
                locationPendingViewportCommand != null -> ViewportCommandSource.LOCATION
                rulerPendingViewportCommand != null -> ViewportCommandSource.RULER
                else -> null
            }

        fun toModel(): MapScreenComponent.Model = MapScreenComponent.Model(
            mapState = mapState,
            availableMapCatalog = toolsState?.availableMapCatalog ?: emptyList(),
            lastCameraSnapshot = lastCameraSnapshot,
            isMapToolsMenuVisible = isMapToolsMenuVisible,
            isAvailableMapsSheetVisible = isAvailableMapsSheetVisible,
            selectedAvailableMap = selectedAvailableMap,
            isMapsOnScreenSheetVisible = isMapsOnScreenSheetVisible,
            selectedOverlayLayer = selectedOverlayLayer,
            editingOverlayOpacityLayer = editingOverlayOpacityLayer,
            myLocationMode = myLocationMode,
            currentLocationMarker = currentLocationMarker,
            pendingLocationRequest = activeLocationRequest,
            isRulerEnabled = isRulerEnabled,
            rulerMeasurement = rulerMeasurement,
            rulerInfoWindow = rulerInfoWindow,
            pendingViewportCommand = pendingViewportCommand,
            isCenterMarkerMenuVisible = isCenterMarkerMenuVisible,
            isCenterMarkerEnabled = isCenterMarkerEnabled,
            isCreatePointSheetVisible = isCreatePointSheetVisible,
            createPointDraft = createPointDraft?.toComponentDraft(),
            drawingMode = drawingMode?.toComponentDrawingMode(),
            shapeDrawingDraft = shapeDrawingDraft?.toComponentDraft(),
            isCreateShapeSheetVisible = isCreateShapeSheetVisible,
            selectedFeatureInfoWindow = selectedFeatureInfoWindow,
        )
    }

    sealed interface Label {
        data class ViewportCommandRequested(val command: MapViewportCommand) : Label
        data class LocationRequestIssued(val request: MapLocationRequest) : Label
        data object DismissToolsMenu : Label
        data object DismissViewportMenu : Label
        data object CenterMarkerMenuOpenRequested : Label
    }

    enum class OverlayTarget(
        val dismissToolsMenu: Boolean,
        val dismissViewportMenu: Boolean,
        val clearFeatureInfoWindow: Boolean,
    ) {
        TOOLS_OVERLAY(
            dismissToolsMenu = false,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
        CENTER_MARKER_MENU(
            dismissToolsMenu = true,
            dismissViewportMenu = false,
            clearFeatureInfoWindow = true,
        ),
        DRAWING_OVERLAY(
            dismissToolsMenu = true,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
        VIEWPORT_EXCLUSIVE_ACTION(
            dismissToolsMenu = false,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
        FEATURE_SELECTION(
            dismissToolsMenu = true,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
    }

    enum class ViewportCommandSource {
        VIEWPORT,
        LOCATION,
        RULER,
    }

    sealed interface ChildState {
        data class Viewport(
            val lastCameraSnapshot: MapCameraSnapshot? = null,
            val pendingCommand: MapViewportCommand? = null,
        ) : ChildState

        data class Tools(
            val availableMapCatalog: List<MapCatalogItem> = emptyList(),
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

        data class Drawing(
            val points: List<MapPoint> = emptyList(),
            val lines: List<MapLine> = emptyList(),
            val polygons: List<MapPolygon> = emptyList(),
            val isCreatePointSheetVisible: Boolean = false,
            val createPointDraft: CreatePointDraft? = null,
            val mode: DrawingMode? = null,
            val shapeDraft: ShapeDrawingDraft? = null,
            val isCreateShapeSheetVisible: Boolean = false,
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
}

internal fun MapRouterStore.CreatePointDraft.toComponentDraft(): MapScreenComponent.CreatePointDraft =
    MapScreenComponent.CreatePointDraft(
        latitudeInput = latitudeInput,
        longitudeInput = longitudeInput,
        titleInput = titleInput,
    )

internal fun MapRouterStore.DrawingMode.toComponentDrawingMode(): MapScreenComponent.DrawingMode = when (this) {
    MapRouterStore.DrawingMode.LINE -> MapScreenComponent.DrawingMode.LINE
    MapRouterStore.DrawingMode.POLYGON -> MapScreenComponent.DrawingMode.POLYGON
}

internal fun MapRouterStore.ShapeDrawingDraft.toComponentDraft(): MapScreenComponent.ShapeDrawingDraft =
    MapScreenComponent.ShapeDrawingDraft(
        mode = mode.toComponentDrawingMode(),
        fixedVertices = fixedVertices,
        titleInput = titleInput,
    )
