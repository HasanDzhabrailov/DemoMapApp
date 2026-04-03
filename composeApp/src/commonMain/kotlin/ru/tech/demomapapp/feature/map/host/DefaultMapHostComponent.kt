package ru.tech.demomapapp.feature.map.host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapScreenUiContract
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.drawing.DefaultDrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingStoreFactory
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStore
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreFactory
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreHolder
import ru.tech.demomapapp.feature.map.location.DefaultLocationComponent
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.location.LocationStoreFactory
import ru.tech.demomapapp.feature.map.ruler.DefaultRulerComponent
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.ruler.RulerStoreFactory
import ru.tech.demomapapp.feature.map.tools.DefaultToolsComponent
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.tools.ToolsStoreFactory
import ru.tech.demomapapp.feature.map.viewport.DefaultViewportComponent
import ru.tech.demomapapp.feature.map.viewport.ViewportComponent
import ru.tech.demomapapp.feature.map.viewport.ViewportStoreFactory

@Suppress("TooManyFunctions")
internal class DefaultMapHostComponent(
    componentContext: ComponentContext,
    initialModel: MapScreenComponent.Model = MapScreenComponent.Model(),
    mapRouterStoreFactory: MapRouterStoreFactory = MapRouterStoreFactory(),
    drawingStoreFactory: DrawingStoreFactory = DrawingStoreFactory(),
    toolsStoreFactory: ToolsStoreFactory = ToolsStoreFactory(),
    locationStoreFactory: LocationStoreFactory = LocationStoreFactory(),
    rulerStoreFactory: RulerStoreFactory = RulerStoreFactory(),
    viewportStoreFactory: ViewportStoreFactory = ViewportStoreFactory(),
) : MapScreenUiContract, ComponentContext by componentContext {
    private var syncedRulerLocation = initialModel.currentLocationMarker
    private var syncedRulerSnapshot = initialModel.lastCameraSnapshot
    private val routerHolder = instanceKeeper.getOrCreate(key = MAP_ROUTER_STORE_HOLDER_KEY) {
        MapRouterStoreHolder(
            mapRouterStoreFactory = mapRouterStoreFactory,
            initialModel = initialModel,
        )
    }

    override val toolsComponent: ToolsComponent = DefaultToolsComponent(
        componentContext = childContext(key = TOOLS_CHILD_CONTEXT_KEY),
        toolsStoreFactory = toolsStoreFactory,
        initialModel = initialModel,
        output = object : ToolsComponent.Output {
            override fun onStateChanged() {
                syncToolsState()
            }

            override fun onLayersChanged(layers: List<MapLayerEntry>) = Unit
        },
    )

    override val drawingComponent: DrawingComponent = DefaultDrawingComponent(
        componentContext = childContext(key = DRAWING_CHILD_CONTEXT_KEY),
        drawingStoreFactory = drawingStoreFactory,
        initialModel = initialModel.toDrawingModel(),
        output = object : DrawingComponent.Output {
            override fun onStateChanged() {
                syncDrawingState()
            }

            override fun onFeatureCreated(feature: DrawingComponent.CreatedFeature) = Unit
        },
    )

    override val rulerComponent: RulerComponent = DefaultRulerComponent(
        componentContext = childContext(key = RULER_CHILD_CONTEXT_KEY),
        rulerStoreFactory = rulerStoreFactory,
        initialModel = initialModel.toRulerModel(),
        output = object : RulerComponent.Output {
            override fun onStateChanged() {
                syncRulerState()
            }

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                onViewportCommandRequested(MapRouterStore.ViewportCommandSource.RULER, command)
            }
        },
    )

    private val viewportComponent: ViewportComponent = DefaultViewportComponent(
        componentContext = childContext(key = VIEWPORT_CHILD_CONTEXT_KEY),
        viewportStoreFactory = viewportStoreFactory,
        initialModel = initialModel.toViewportModel(),
        output = object : ViewportComponent.Output {
            override fun onStateChanged() {
                syncViewportState()
                syncCenterMarkerState()
                syncRulerInputs()
            }

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                onViewportCommandRequested(MapRouterStore.ViewportCommandSource.VIEWPORT, command)
            }
        },
    )

    override val locationComponent: LocationComponent = DefaultLocationComponent(
        componentContext = childContext(key = LOCATION_CHILD_CONTEXT_KEY),
        locationStoreFactory = locationStoreFactory,
        initialModel = initialModel.toLocationModel(),
        output = object : LocationComponent.Output {
            override fun onStateChanged() {
                syncLocationState()
                syncRulerInputs()
            }

            override fun onLocationUpdated(location: MapLocationMarker?) = Unit

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                onViewportCommandRequested(MapRouterStore.ViewportCommandSource.LOCATION, command)
            }

            override fun onLocationRequestIssued(request: MapLocationRequest) {
                syncLocationState()
            }
        },
    )

    init {
        syncAllStates()
    }

    override val model: Value<MapScreenComponent.Model> = routerHolder.model

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        viewportComponent.onCameraIdle(snapshot)
        drawingComponent.onCameraPositionUpdated(snapshot)
        locationComponent.onCameraSnapshotReceived(snapshot)
    }

    override fun onMapToolsClick() {
        dismissViewportMenuIfVisible()
        toolsComponent.onMapToolsClick()
    }

    override fun onMapToolsDismiss() = toolsComponent.onMapToolsDismiss()

    override fun onZoomInClick() = viewportComponent.onZoomInClick()

    override fun onZoomOutClick() = viewportComponent.onZoomOutClick()

    override fun onAvailableMapsClick() {
        dismissViewportMenuIfVisible()
        toolsComponent.onAvailableMapsClick()
    }

    override fun onAvailableMapsDismiss() = toolsComponent.onAvailableMapsDismiss()

    override fun onAvailableMapSelect(mapId: String) = toolsComponent.onAvailableMapSelect(mapId)

    override fun onAvailableMapConfirm() = toolsComponent.onAvailableMapConfirm()

    override fun onAvailableMapSelectionDismiss() = toolsComponent.onAvailableMapSelectionDismiss()

    override fun onMapsOnScreenClick() {
        dismissViewportMenuIfVisible()
        toolsComponent.onMapsOnScreenClick()
    }

    override fun onMapsOnScreenDismiss() = toolsComponent.onMapsOnScreenDismiss()

    override fun onMapLayerActionsClick(layerId: String) = toolsComponent.onLayerActionsClick(layerId)

    override fun onMapLayerActionsDismiss() = toolsComponent.onLayerActionsDismiss()

    override fun onMoveLayerUpClick() = toolsComponent.onMoveLayerUpClick()

    override fun onMoveLayerDownClick() = toolsComponent.onMoveLayerDownClick()

    override fun onRemoveLayerClick() = toolsComponent.onRemoveLayerClick()

    override fun onLayerOpacityClick() = toolsComponent.onLayerOpacityClick()

    override fun onLayerOpacityChange(value: Float) = toolsComponent.onLayerOpacityChange(value)

    override fun onLayerOpacityDismiss() = toolsComponent.onLayerOpacityDismiss()

    override fun onGpsToggle() = locationComponent.onGpsToggle()

    override fun onMyLocationClick() = locationComponent.onMyLocationClick()

    override fun onCurrentLocationFocusClick() = locationComponent.onCurrentLocationFocusClick()

    override fun onLocationRequestConsumed() = locationComponent.onLocationRequestConsumed()

    override fun onLocationResult(result: LocationRequestResult) = locationComponent.onLocationResult(result)

    override fun onRulerToggle() {
        dismissViewportMenuIfVisible()
        rulerComponent.onToggleClicked()
    }

    override fun onViewportCommandConsumed() {
        when (currentViewportCommandSource()) {
            MapRouterStore.ViewportCommandSource.VIEWPORT -> {
                viewportComponent.onViewportCommandConsumed()
                routerHolder.accept(MapRouterStore.Intent.ViewportCommandConsumed(MapRouterStore.ViewportCommandSource.VIEWPORT))
                syncViewportState()
            }

            MapRouterStore.ViewportCommandSource.LOCATION -> {
                routerHolder.accept(MapRouterStore.Intent.ViewportCommandConsumed(MapRouterStore.ViewportCommandSource.LOCATION))
            }

            MapRouterStore.ViewportCommandSource.RULER -> {
                routerHolder.accept(MapRouterStore.Intent.ViewportCommandConsumed(MapRouterStore.ViewportCommandSource.RULER))
            }

            null -> Unit
        }
    }

    override fun onCenterMarkerClick() {
        if (model.value.drawingMode != null) {
            return
        }
        dismissToolsMenuIfVisible()
        if (model.value.selectedFeatureInfoWindow != null) {
            routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)
        }
        viewportComponent.onCenterMarkerClick()
    }

    override fun onCenterMarkerMenuDismiss() = viewportComponent.onCenterMarkerMenuDismiss()

    override fun onCreatePointClick() {
        dismissToolsMenuIfVisible()
        dismissViewportMenuIfVisible()
        drawingComponent.onCreatePointClick()
    }

    override fun onCreateLineClick() {
        dismissToolsMenuIfVisible()
        dismissViewportMenuIfVisible()
        drawingComponent.onCreateLineClick()
    }

    override fun onCreatePolygonClick() {
        dismissToolsMenuIfVisible()
        dismissViewportMenuIfVisible()
        drawingComponent.onCreatePolygonClick()
    }

    override fun onCreatePointLatitudeChange(value: String) = drawingComponent.onCreatePointLatitudeChange(value)

    override fun onCreatePointLongitudeChange(value: String) = drawingComponent.onCreatePointLongitudeChange(value)

    override fun onCreatePointTitleChange(value: String) = drawingComponent.onCreatePointTitleChange(value)

    override fun onCreatePointConfirm() = drawingComponent.onCreatePointConfirm()

    override fun onCreatePointSheetDismiss() = drawingComponent.onCreatePointSheetDismiss()

    override fun onDrawingAddPositionClick() {
        dismissViewportMenuIfVisible()
        drawingComponent.onDrawingAddPositionClick()
    }

    override fun onDrawingRemoveLastPositionClick() = drawingComponent.onDrawingRemoveLastPositionClick()

    override fun onDrawingDetailsClick() = drawingComponent.onDrawingDetailsClick()

    override fun onDrawingDismiss() = drawingComponent.onDrawingDismiss()

    override fun onCreateShapeTitleChange(value: String) = drawingComponent.onCreateShapeTitleChange(value)

    override fun onCreateShapeConfirm() = drawingComponent.onCreateShapeConfirm()

    override fun onCreateShapeSheetDismiss() = drawingComponent.onCreateShapeSheetDismiss()

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) {
        dismissToolsMenuIfVisible()
        dismissViewportMenuIfVisible()
        routerHolder.accept(
            MapRouterStore.Intent.FeatureClicked(
                featureKey = featureKey,
                featureType = featureType,
                anchor = anchor,
            ),
        )
    }

    override fun onFeatureInfoWindowDismiss() {
        routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)
    }

    private fun onViewportCommandRequested(
        source: MapRouterStore.ViewportCommandSource,
        command: MapViewportCommand,
    ) {
        routerHolder.accept(MapRouterStore.Intent.ViewportCommandUpdated(source = source, command = command))
    }

    private fun syncAllStates() {
        syncToolsState()
        syncDrawingState()
        syncLocationState()
        syncRulerState()
        syncViewportState()
        syncCenterMarkerState()
        syncRulerInputs()
    }

    private fun syncToolsState() {
        routerHolder.accept(
            MapRouterStore.Intent.ToolsStateUpdated(
                toolsComponent.model.value.toRouterState(toolsComponent.childSlot.value.child?.instance),
            ),
        )
    }

    private fun syncDrawingState() {
        routerHolder.accept(
            MapRouterStore.Intent.DrawingStateUpdated(
                drawingComponent.model.value.toRouterState(
                    activePointSheetChild = drawingComponent.pointSheetSlot.value.child?.instance,
                    activeShapeSheetChild = drawingComponent.shapeSheetSlot.value.child?.instance,
                ),
            ),
        )
    }

    private fun syncLocationState() {
        routerHolder.accept(MapRouterStore.Intent.LocationStateUpdated(locationComponent.model.value.toRouterState()))
    }

    private fun syncRulerState() {
        routerHolder.accept(MapRouterStore.Intent.RulerStateUpdated(rulerComponent.model.value.toRouterState()))
    }

    private fun syncViewportState() {
        routerHolder.accept(MapRouterStore.Intent.ViewportStateUpdated(viewportComponent.model.value.toViewportRouterState()))
    }

    private fun syncCenterMarkerState() {
        routerHolder.accept(
            MapRouterStore.Intent.CenterMarkerStateUpdated(viewportComponent.model.value.toCenterMarkerRouterState()),
        )
    }

    private fun currentViewportCommandSource(): MapRouterStore.ViewportCommandSource? = when {
        routerHolder.state.viewportPendingCommand != null -> MapRouterStore.ViewportCommandSource.VIEWPORT
        routerHolder.state.locationPendingViewportCommand != null -> MapRouterStore.ViewportCommandSource.LOCATION
        routerHolder.state.rulerPendingViewportCommand != null -> MapRouterStore.ViewportCommandSource.RULER
        else -> null
    }

    private fun syncRulerInputs() {
        val viewportSnapshot = viewportComponent.model.value.cameraSnapshot
        if (viewportSnapshot != syncedRulerSnapshot) {
            viewportSnapshot?.let(rulerComponent::onCameraSnapshotReceived)
            syncedRulerSnapshot = viewportSnapshot
            syncRulerState()
        }

        val locationMarker = locationComponent.model.value.currentMarker
        if (locationMarker != syncedRulerLocation) {
            rulerComponent.onLocationUpdated(locationMarker)
            syncedRulerLocation = locationMarker
            syncRulerState()
        }
    }

    private fun dismissToolsMenuIfVisible() {
        if (toolsComponent.childSlot.value.child?.instance is ToolsComponent.Child.Menu) {
            toolsComponent.onMapToolsDismiss()
            syncToolsState()
        }
    }

    private fun dismissViewportMenuIfVisible() {
        if (viewportComponent.childSlot.value.child?.instance is ViewportComponent.Child.Menu) {
            viewportComponent.onCenterMarkerMenuDismiss()
            syncCenterMarkerState()
        }
    }

    private companion object {
        const val MAP_ROUTER_STORE_HOLDER_KEY = "DefaultMapHostComponent.mapRouterStoreHolder"
        const val TOOLS_CHILD_CONTEXT_KEY = "tools"
        const val DRAWING_CHILD_CONTEXT_KEY = "drawing"
        const val LOCATION_CHILD_CONTEXT_KEY = "location"
        const val RULER_CHILD_CONTEXT_KEY = "ruler"
        const val VIEWPORT_CHILD_CONTEXT_KEY = "viewport"
    }
}
