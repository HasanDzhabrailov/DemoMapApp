package ru.tech.demomapapp.feature.map.mapscreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStore
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreFactory
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreHolder
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.viewport.ViewportComponent

@Suppress("TooManyFunctions")
internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    initialModel: MapScreenComponent.Model = MapScreenComponent.Model(),
    private val mapRouterStoreFactory: MapRouterStoreFactory = MapRouterStoreFactory(),
    override val toolsComponent: ToolsComponent,
    override val drawingComponent: DrawingComponent,
    override val locationComponent: LocationComponent,
    override val rulerComponent: RulerComponent,
    override val viewportComponent: ViewportComponent,
) : MapScreenUiComponent, ComponentContext by componentContext {
    private val routerHolder = instanceKeeper.getOrCreate(key = MAP_ROUTER_STORE_HOLDER_KEY) {
        MapRouterStoreHolder(
            mapRouterStoreFactory = mapRouterStoreFactory,
            initialModel = initialModel,
        )
    }
    private var bridge: MapScreenRouterBridge? = null

    override val model: Value<MapScreenComponent.Model> = routerHolder.model

    init {
        bridge = MapScreenRouterBridge(
            routerHolder = routerHolder,
            toolsComponent = toolsComponent,
            drawingComponent = drawingComponent,
            locationComponent = locationComponent,
            rulerComponent = rulerComponent,
            viewportComponent = viewportComponent,
        )
        bridge?.syncAllStates()
    }

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        viewportComponent.onCameraIdle(snapshot)
        drawingComponent.onCameraPositionUpdated(snapshot)
        locationComponent.onCameraSnapshotReceived(snapshot)
    }

    override fun onMapToolsClick() = runToolsAction(
        dismissViewportMenu = true,
        action = toolsComponent::onMapToolsClick,
    )

    override fun onMapToolsDismiss() = toolsComponent.onMapToolsDismiss()

    override fun onZoomInClick() = viewportComponent.onZoomInClick()

    override fun onZoomOutClick() = viewportComponent.onZoomOutClick()

    override fun onAvailableMapsClick() = runToolsAction(
        dismissViewportMenu = true,
        action = toolsComponent::onAvailableMapsClick,
    )

    override fun onAvailableMapsDismiss() = toolsComponent.onAvailableMapsDismiss()

    override fun onAvailableMapSelect(mapId: String) = toolsComponent.onAvailableMapSelect(mapId)

    override fun onAvailableMapConfirm() = toolsComponent.onAvailableMapConfirm()

    override fun onAvailableMapSelectionDismiss() = toolsComponent.onAvailableMapSelectionDismiss()

    override fun onMapsOnScreenClick() = runToolsAction(
        dismissViewportMenu = true,
        action = toolsComponent::onMapsOnScreenClick,
    )

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

    override fun onRulerToggle() = runRulerAction(
        dismissViewportMenu = true,
        action = rulerComponent::onToggleClicked,
    )

    override fun onViewportCommandConsumed() {
        bridge?.consumeViewportCommand()
    }

    override fun onCenterMarkerClick() {
        if (routerHolder.model.value.drawingMode != null) {
            return
        }
        bridge?.dismissToolsMenuIfVisible()
        bridge?.dismissFeatureInfoWindowIfVisible()
        viewportComponent.onCenterMarkerClick()
    }

    override fun onCenterMarkerMenuDismiss() = viewportComponent.onCenterMarkerMenuDismiss()

    override fun onCreatePointClick() = runDrawingAction(
        dismissToolsMenu = true,
        dismissViewportMenu = true,
        action = drawingComponent::onCreatePointClick,
    )

    override fun onCreateLineClick() = runDrawingAction(
        dismissToolsMenu = true,
        dismissViewportMenu = true,
        action = drawingComponent::onCreateLineClick,
    )

    override fun onCreatePolygonClick() = runDrawingAction(
        dismissToolsMenu = true,
        dismissViewportMenu = true,
        action = drawingComponent::onCreatePolygonClick,
    )

    override fun onCreatePointLatitudeChange(value: String) = drawingComponent.onCreatePointLatitudeChange(value)

    override fun onCreatePointLongitudeChange(value: String) = drawingComponent.onCreatePointLongitudeChange(value)

    override fun onCreatePointTitleChange(value: String) = drawingComponent.onCreatePointTitleChange(value)

    override fun onCreatePointConfirm() = drawingComponent.onCreatePointConfirm()

    override fun onCreatePointSheetDismiss() = drawingComponent.onCreatePointSheetDismiss()

    override fun onDrawingAddPositionClick() = runDrawingAction(
        dismissViewportMenu = true,
        action = drawingComponent::onDrawingAddPositionClick,
    )

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
        bridge?.dismissToolsMenuIfVisible()
        bridge?.dismissViewportMenuIfVisible()
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

    internal fun onLocationStateChanged() {
        bridge?.onLocationStateChanged()
    }

    internal fun onToolsStateChanged() {
        bridge?.onToolsStateChanged()
    }

    internal fun onDrawingStateChanged() {
        bridge?.onDrawingStateChanged()
    }

    internal fun onRulerStateChanged() {
        bridge?.onRulerStateChanged()
    }

    internal fun onViewportStateChanged() {
        bridge?.onViewportStateChanged()
    }

    internal fun onLocationRequestIssued() {
        bridge?.onLocationRequestIssued()
    }

    internal fun requestViewportCommand(
        source: MapRouterStore.ViewportCommandSource,
        command: MapViewportCommand,
    ) {
        bridge?.requestViewportCommand(source, command)
    }

    private inline fun runToolsAction(dismissViewportMenu: Boolean = false, action: () -> Unit) {
        if (dismissViewportMenu) {
            bridge?.dismissViewportMenuIfVisible()
        }
        action()
    }

    private inline fun runRulerAction(dismissViewportMenu: Boolean = false, action: () -> Unit) {
        if (dismissViewportMenu) {
            bridge?.dismissViewportMenuIfVisible()
        }
        action()
    }

    private inline fun runDrawingAction(
        dismissToolsMenu: Boolean = false,
        dismissViewportMenu: Boolean = false,
        action: () -> Unit,
    ) {
        if (dismissToolsMenu) {
            bridge?.dismissToolsMenuIfVisible()
        }
        if (dismissViewportMenu) {
            bridge?.dismissViewportMenuIfVisible()
        }
        action()
    }

    private companion object {
        const val MAP_ROUTER_STORE_HOLDER_KEY = "DefaultMapScreenComponent.mapRouterStoreHolder"
    }
}
