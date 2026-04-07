package ru.tech.demomapapp.feature.map.host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.DrawingUiContract
import ru.tech.demomapapp.feature.map.api.LocationUiContract
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
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

    // Child components with default initial state (not derived from parent model)
    private val toolsComponent: ToolsComponent = DefaultToolsComponent(
        childContext("tools"), toolsStoreFactory,
        output = object : ToolsComponent.Output {
            override fun onStateChanged() = Unit
            override fun onLayersChanged(layers: List<MapLayerEntry>) = Unit
        },
    )

    private val drawingComponent: DrawingComponent = DefaultDrawingComponent(
        childContext("drawing"), drawingStoreFactory,
        output = object : DrawingComponent.Output {
            override fun onStateChanged() = Unit
            override fun onFeatureCreated(feature: DrawingComponent.CreatedFeature) = Unit
        },
    )

    private val rulerComponent: RulerComponent = DefaultRulerComponent(
        childContext("ruler"), rulerStoreFactory,
        inputSource = RulerComponent.InputSource { callback ->
            // Subscribe to location and viewport models to provide parent state
            var currentLocation: MapLocationMarker? = null
            var currentSnapshot: MapCameraSnapshot? = null
            var hasEmitted = false

            val locationDisposable = locationComponent.model.subscribe { locationModel ->
                currentLocation = locationModel.currentMarker
                if (!hasEmitted || currentSnapshot != null) {
                    hasEmitted = true
                    callback(RulerComponent.ParentState(currentLocation, currentSnapshot))
                }
            }

            val viewportDisposable = viewportComponent.model.subscribe { viewportModel ->
                currentSnapshot = viewportModel.cameraSnapshot
                if (!hasEmitted || currentLocation != null) {
                    hasEmitted = true
                    callback(RulerComponent.ParentState(currentLocation, currentSnapshot))
                }
            }

            // Return disposable that cleans up both subscriptions
            com.arkivanov.mvikotlin.core.rx.Disposable {
                locationDisposable.cancel()
                viewportDisposable.cancel()
            }
        },
        output = object : RulerComponent.Output {
            override fun onStateChanged() = Unit
            override fun onViewportCommandRequested(cmd: MapViewportCommand) {
                onViewportCommandRequested(MapRouterStore.ViewportCommandSource.RULER, cmd)
            }
        },
    )

    private val viewportComponent: ViewportComponent = DefaultViewportComponent(
        childContext("viewport"), viewportStoreFactory,
        output = object : ViewportComponent.Output {
            override fun onStateChanged() = Unit
            override fun onViewportCommandRequested(cmd: MapViewportCommand) {
                onViewportCommandRequested(MapRouterStore.ViewportCommandSource.VIEWPORT, cmd)
            }
        },
    )

    private val locationComponent: LocationComponent = DefaultLocationComponent(
        childContext("location"), locationStoreFactory,
        output = object : LocationComponent.Output {
            override fun onStateChanged() = Unit
            override fun onLocationUpdated(location: MapLocationMarker?) = Unit
            override fun onViewportCommandRequested(cmd: MapViewportCommand) {
                onViewportCommandRequested(MapRouterStore.ViewportCommandSource.LOCATION, cmd)
            }
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

    override fun onRulerToggle() {
        // Toggle ruler state and notify children
        val newEnabledState = !routerHolder.state.isRulerEnabled
        routerHolder.accept(MapRouterStore.Intent.RulerEnabledUpdated(newEnabledState))
        routeOverlay(MapRouterStore.OverlayTarget.VIEWPORT_EXCLUSIVE_ACTION, rulerComponent::onToggleClicked)
    }

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

    override fun onFeatureClick(
        points: List<MapPoint>,
        lines: List<MapLine>,
        polygons: List<MapPolygon>,
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) {
        routeOverlay(MapRouterStore.OverlayTarget.FEATURE_SELECTION)
        routerHolder.accept(
            MapRouterStore.Intent.FeatureClicked(
                points = points,
                lines = lines,
                polygons = polygons,
                featureKey = featureKey,
                featureType = featureType,
                anchor = anchor,
            )
        )
    }

    override fun onFeatureInfoWindowDismiss() = routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)

    private fun onViewportCommandRequested(s: MapRouterStore.ViewportCommandSource, c: MapViewportCommand) {
        routerHolder.accept(MapRouterStore.Intent.ViewportCommandUpdated(s, c))
    }

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