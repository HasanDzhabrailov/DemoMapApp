package ru.tech.demomapapp.feature.map.host

import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.drawing.CreatePointDraft as DrawingCreatePointDraft
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingMode as InternalDrawingMode
import ru.tech.demomapapp.feature.map.drawing.DrawingModel
import ru.tech.demomapapp.feature.map.drawing.ShapeDrawingDraft as InternalShapeDrawingDraft
import ru.tech.demomapapp.feature.map.location.LocationModel
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStore
import ru.tech.demomapapp.feature.map.ruler.RulerModel
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.tools.ToolsModel
import ru.tech.demomapapp.feature.map.viewport.ViewportModel

internal fun ToolsModel.toRouterState(activeChild: ToolsComponent.Child?): MapRouterStore.ChildState.Tools = MapRouterStore.ChildState.Tools(
    availableMapCatalog = availableMapCatalog,
    selectedMapStyle = selectedStyle,
    overlayLayers = layers,
    isMapToolsMenuVisible = activeChild is ToolsComponent.Child.Menu,
    isAvailableMapsSheetVisible =
        activeChild is ToolsComponent.Child.AvailableMaps ||
            activeChild is ToolsComponent.Child.ConfirmAddMap,
    selectedAvailableMap = selectedAvailableMap,
    isMapsOnScreenSheetVisible =
        activeChild is ToolsComponent.Child.MapsOnScreen ||
            activeChild is ToolsComponent.Child.LayerActions ||
            activeChild is ToolsComponent.Child.LayerOpacity,
    selectedOverlayLayer = selectedOverlayLayer,
    editingOverlayOpacityLayer = editingOverlayOpacityLayer,
)

internal fun DrawingModel.toRouterState(
    activePointSheetChild: DrawingComponent.PointSheetChild?,
    activeShapeSheetChild: DrawingComponent.ShapeSheetChild?,
): MapRouterStore.ChildState.Drawing = MapRouterStore.ChildState.Drawing(
    points = points,
    lines = lines,
    polygons = polygons,
    isCreatePointSheetVisible = activePointSheetChild is DrawingComponent.PointSheetChild.Content,
    createPointDraft = createPointDraft?.let { draft ->
        MapRouterStore.CreatePointDraft(
            latitudeInput = draft.latitudeInput,
            longitudeInput = draft.longitudeInput,
            titleInput = draft.titleInput,
        )
    },
    mode = drawingMode?.let { mode ->
        when (mode) {
            ru.tech.demomapapp.feature.map.drawing.DrawingMode.LINE -> MapRouterStore.DrawingMode.LINE
            ru.tech.demomapapp.feature.map.drawing.DrawingMode.POLYGON -> MapRouterStore.DrawingMode.POLYGON
        }
    },
    shapeDraft = shapeDrawingDraft?.let { draft ->
        MapRouterStore.ShapeDrawingDraft(
            mode = when (draft.mode) {
                ru.tech.demomapapp.feature.map.drawing.DrawingMode.LINE -> MapRouterStore.DrawingMode.LINE
                ru.tech.demomapapp.feature.map.drawing.DrawingMode.POLYGON -> MapRouterStore.DrawingMode.POLYGON
            },
            fixedVertices = draft.fixedVertices,
            titleInput = draft.titleInput,
        )
    },
    isCreateShapeSheetVisible = activeShapeSheetChild is DrawingComponent.ShapeSheetChild.Content,
)

internal fun LocationModel.toRouterState(): MapRouterStore.ChildState.Location {
    return MapRouterStore.ChildState.Location(
        myLocationMode = mode,
        currentLocationMarker = currentMarker,
        activeLocationRequest = pendingRequest,
    )
}

internal fun RulerModel.toRouterState(): MapRouterStore.ChildState.Ruler = MapRouterStore.ChildState.Ruler(
    isRulerEnabled = isEnabled,
    rulerMeasurement = measurement,
    rulerInfoWindow = infoWindow,
)

internal fun ViewportModel.toViewportRouterState(): MapRouterStore.ChildState.Viewport =
    MapRouterStore.ChildState.Viewport(
        lastCameraSnapshot = cameraSnapshot,
        pendingCommand = pendingCommand,
    )

internal fun ViewportModel.toCenterMarkerRouterState(): MapRouterStore.ChildState.CenterMarker =
    MapRouterStore.ChildState.CenterMarker(isMenuVisible = isCenterMarkerMenuVisible)

internal fun MapScreenComponent.Model.toDrawingModel(): DrawingModel = DrawingModel(
    points = mapState.points,
    lines = mapState.lines,
    polygons = mapState.polygons,
    isCreatePointSheetVisible = isCreatePointSheetVisible,
    createPointDraft = createPointDraft?.let { draft ->
        DrawingCreatePointDraft(
            latitudeInput = draft.latitudeInput,
            longitudeInput = draft.longitudeInput,
            titleInput = draft.titleInput,
        )
    },
    drawingMode = drawingMode?.toInternalDrawingMode(),
    shapeDrawingDraft = shapeDrawingDraft?.let { draft ->
        InternalShapeDrawingDraft(
            mode = draft.mode.toInternalDrawingMode(),
            fixedVertices = draft.fixedVertices,
            titleInput = draft.titleInput,
        )
    },
    isCreateShapeSheetVisible = isCreateShapeSheetVisible,
    lastCameraSnapshot = lastCameraSnapshot,
)

internal fun MapScreenComponent.Model.toLocationModel(): LocationModel = LocationModel(
    mode = myLocationMode,
    currentMarker = currentLocationMarker,
    pendingRequest = pendingLocationRequest,
)

internal fun MapScreenComponent.Model.toRulerModel(): RulerModel = RulerModel(
    isEnabled = isRulerEnabled,
    currentLocation = currentLocationMarker,
    lastCameraSnapshot = lastCameraSnapshot,
    measurement = rulerMeasurement,
    infoWindow = rulerInfoWindow,
)

internal fun MapScreenComponent.Model.toViewportModel(): ViewportModel = ViewportModel(
    cameraSnapshot = lastCameraSnapshot,
    pendingCommand = pendingViewportCommand,
    isCenterMarkerMenuVisible = isCenterMarkerMenuVisible,
)

private fun MapScreenComponent.DrawingMode.toInternalDrawingMode(): InternalDrawingMode = when (this) {
    MapScreenComponent.DrawingMode.LINE -> InternalDrawingMode.LINE
    MapScreenComponent.DrawingMode.POLYGON -> InternalDrawingMode.POLYGON
}
