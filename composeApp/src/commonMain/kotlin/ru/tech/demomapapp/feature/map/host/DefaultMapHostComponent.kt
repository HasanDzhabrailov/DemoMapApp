package ru.tech.demomapapp.feature.map.host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.DrawingUiContract
import ru.tech.demomapapp.feature.map.api.LocationUiContract
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapScreenUiContract
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.RulerUiContract
import ru.tech.demomapapp.feature.map.api.ToolsUiContract
import ru.tech.demomapapp.feature.map.api.ViewportUiContract
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
    private val routerHolder = instanceKeeper.getOrCreate(KEY) {
        MapRouterStoreHolder(mapRouterStoreFactory, initialModel)
    }
    private val toolsComponent: ToolsComponent = DefaultToolsComponent(
        childContext("tools"), toolsStoreFactory, initialModel,
        output = object : ToolsComponent.Output {
            override fun onStateChanged() = Unit
            override fun onLayersChanged(layers: List<MapLayerEntry>) = Unit
        },
    )
    private val drawingComponent: DrawingComponent = DefaultDrawingComponent(
        childContext("drawing"), drawingStoreFactory, initialModel.toDrawingModel(),
        output = object : DrawingComponent.Output {
            override fun onStateChanged() = Unit
            override fun onFeatureCreated(feature: DrawingComponent.CreatedFeature) = Unit
        },
    )
    private val rulerComponent: RulerComponent = DefaultRulerComponent(
        childContext("ruler"), rulerStoreFactory, initialModel.toRulerModel(),
        inputSource = RulerComponent.InputSource { cb -> routerHolder.states { cb(RulerComponent.ParentState(it.currentLocationMarker, it.lastCameraSnapshot)) } },
        output = object : RulerComponent.Output {
            override fun onStateChanged() = Unit
            override fun onViewportCommandRequested(cmd: MapViewportCommand) { onViewportCommandRequested(MapRouterStore.ViewportCommandSource.RULER, cmd) }
        },
    )
    private val viewportComponent: ViewportComponent = DefaultViewportComponent(
        childContext("viewport"), viewportStoreFactory, initialModel.toViewportModel(),
        output = object : ViewportComponent.Output {
            override fun onStateChanged() = Unit
            override fun onViewportCommandRequested(cmd: MapViewportCommand) { onViewportCommandRequested(MapRouterStore.ViewportCommandSource.VIEWPORT, cmd) }
        },
    )
    private val locationComponent: LocationComponent = DefaultLocationComponent(
        childContext("location"), locationStoreFactory, initialModel.toLocationModel(),
        output = object : LocationComponent.Output {
            override fun onStateChanged() = Unit
            override fun onLocationUpdated(location: MapLocationMarker?) = Unit
            override fun onViewportCommandRequested(cmd: MapViewportCommand) { onViewportCommandRequested(MapRouterStore.ViewportCommandSource.LOCATION, cmd) }
            override fun onLocationRequestIssued(request: MapLocationRequest) = Unit
        },
    )

    // Expose via narrow UI contracts
    override val toolsUi: ToolsUiContract = toolsComponent
    override val drawingUi: DrawingUiContract = drawingComponent
    override val rulerUi: RulerUiContract = rulerComponent
    override val viewportUi: ViewportUiContract = viewportComponent
    override val locationUi: LocationUiContract = locationComponent
    init {
        routerHolder.labels(::handleRouterLabel)
        subscribeToChildStates()
    }
    private fun subscribeToChildStates() {
        toolsComponent.model.subscribe { routerHolder.accept(MapRouterStore.Intent.ToolsStateUpdated(it.toRouterState(toolsComponent.childSlot.value.child?.instance))) }
        drawingComponent.model.subscribe { routerHolder.accept(MapRouterStore.Intent.DrawingStateUpdated(it.toRouterState(drawingComponent.pointSheetSlot.value.child?.instance, drawingComponent.shapeSheetSlot.value.child?.instance))) }
        locationComponent.model.subscribe { routerHolder.accept(MapRouterStore.Intent.LocationStateUpdated(it.toRouterState())) }
        rulerComponent.model.subscribe { routerHolder.accept(MapRouterStore.Intent.RulerStateUpdated(it.toRouterState())) }
        viewportComponent.model.subscribe {
            routerHolder.accept(MapRouterStore.Intent.ViewportStateUpdated(it.toViewportRouterState()))
            routerHolder.accept(MapRouterStore.Intent.CenterMarkerStateUpdated(it.toCenterMarkerRouterState()))
        }
    }
    override val model: Value<MapScreenComponent.Model> = routerHolder.model
    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        viewportComponent.onCameraIdle(snapshot)
        drawingComponent.onCameraPositionUpdated(snapshot)
        locationComponent.onCameraSnapshotReceived(snapshot)
    }
    override fun onMapToolsClick() = routeOverlay(MapRouterStore.OverlayTarget.TOOLS_OVERLAY, toolsComponent::onMapToolsClick)
    override fun onAvailableMapsClick() = routeOverlay(MapRouterStore.OverlayTarget.TOOLS_OVERLAY, toolsComponent::onAvailableMapsClick)
    override fun onMapsOnScreenClick() = routeOverlay(MapRouterStore.OverlayTarget.TOOLS_OVERLAY, toolsComponent::onMapsOnScreenClick)
    override fun onRulerToggle() = routeOverlay(MapRouterStore.OverlayTarget.VIEWPORT_EXCLUSIVE_ACTION, rulerComponent::onToggleClicked)
    override fun onViewportCommandConsumed() {
        routerHolder.accept(MapRouterStore.Intent.ViewportCommandConsumed)
        viewportComponent.onViewportCommandConsumed()
    }
    override fun onCenterMarkerClick() {
        routeOverlay(MapRouterStore.OverlayTarget.CENTER_MARKER_MENU)
        routerHolder.accept(MapRouterStore.Intent.CenterMarkerClicked)
    }
    override fun onCreatePointClick() = routeOverlay(MapRouterStore.OverlayTarget.DRAWING_OVERLAY, drawingComponent::onCreatePointClick)
    override fun onCreateLineClick() = routeOverlay(MapRouterStore.OverlayTarget.DRAWING_OVERLAY, drawingComponent::onCreateLineClick)
    override fun onCreatePolygonClick() = routeOverlay(MapRouterStore.OverlayTarget.DRAWING_OVERLAY, drawingComponent::onCreatePolygonClick)
    override fun onDrawingAddPositionClick() = routeOverlay(MapRouterStore.OverlayTarget.VIEWPORT_EXCLUSIVE_ACTION, drawingComponent::onDrawingAddPositionClick)
    override fun onFeatureClick(k: String, t: MapScreenComponent.FeatureType, a: MapScreenComponent.FeatureInfoWindowAnchor) {
        routeOverlay(MapRouterStore.OverlayTarget.FEATURE_SELECTION)
        routerHolder.accept(MapRouterStore.Intent.FeatureClicked(k, t, a))
    }
    override fun onFeatureInfoWindowDismiss() = routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)
    private fun onViewportCommandRequested(s: MapRouterStore.ViewportCommandSource, c: MapViewportCommand) { routerHolder.accept(MapRouterStore.Intent.ViewportCommandUpdated(s, c)) }
    private fun routeOverlay(t: MapRouterStore.OverlayTarget) = routerHolder.accept(MapRouterStore.Intent.OverlayInteractionRequested(t))
    private fun routeOverlay(t: MapRouterStore.OverlayTarget, a: () -> Unit) { routeOverlay(t); a() }
    private fun handleRouterLabel(l: MapRouterStore.Label) = when (l) {
        MapRouterStore.Label.DismissToolsMenu -> toolsComponent.onMapToolsDismiss()
        MapRouterStore.Label.DismissViewportMenu -> viewportComponent.onCenterMarkerMenuDismiss()
        MapRouterStore.Label.CenterMarkerMenuOpenRequested -> viewportComponent.onCenterMarkerClick()
        is MapRouterStore.Label.LocationRequestIssued, is MapRouterStore.Label.ViewportCommandRequested -> Unit
    }
    private companion object {
        const val KEY = "DefaultMapHostComponent.mapRouterStoreHolder"
    }
}
