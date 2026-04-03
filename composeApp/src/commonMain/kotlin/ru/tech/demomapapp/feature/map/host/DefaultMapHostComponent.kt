package ru.tech.demomapapp.feature.map.host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.drawing.DefaultDrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingStoreFactory
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStore
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreFactory
import ru.tech.demomapapp.feature.map.location.DefaultLocationComponent
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.location.LocationStoreFactory
import ru.tech.demomapapp.feature.map.mapscreen.DefaultMapScreenComponent
import ru.tech.demomapapp.feature.map.mapscreen.MapScreenUiComponent
import ru.tech.demomapapp.feature.map.mapscreen.toDrawingModel
import ru.tech.demomapapp.feature.map.mapscreen.toLocationModel
import ru.tech.demomapapp.feature.map.mapscreen.toRulerModel
import ru.tech.demomapapp.feature.map.mapscreen.toViewportModel
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
) : MapScreenUiComponent, ComponentContext by componentContext {
    private var screenCallbacks: ScreenCallbacks? = null

    override val toolsComponent: ToolsComponent = DefaultToolsComponent(
        componentContext = childContext(key = TOOLS_CHILD_CONTEXT_KEY),
        toolsStoreFactory = toolsStoreFactory,
        initialModel = initialModel,
        output = object : ToolsComponent.Output {
            override fun onStateChanged() {
                screenCallbacks?.onToolsStateChanged()
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
                screenCallbacks?.onDrawingStateChanged()
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
                screenCallbacks?.onRulerStateChanged()
            }

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                screenCallbacks?.requestViewportCommand(command, ScreenCallbacks.ViewportCommandSource.RULER)
            }
        },
    )

    override val viewportComponent: ViewportComponent = DefaultViewportComponent(
        componentContext = childContext(key = VIEWPORT_CHILD_CONTEXT_KEY),
        viewportStoreFactory = viewportStoreFactory,
        initialModel = initialModel.toViewportModel(),
        output = object : ViewportComponent.Output {
            override fun onStateChanged() {
                screenCallbacks?.onViewportStateChanged()
            }

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                screenCallbacks?.requestViewportCommand(command, ScreenCallbacks.ViewportCommandSource.VIEWPORT)
            }
        },
    )

    override val locationComponent: LocationComponent = DefaultLocationComponent(
        componentContext = childContext(key = LOCATION_CHILD_CONTEXT_KEY),
        locationStoreFactory = locationStoreFactory,
        initialModel = initialModel.toLocationModel(),
        output = object : LocationComponent.Output {
            override fun onStateChanged() {
                screenCallbacks?.onLocationStateChanged()
            }

            override fun onLocationUpdated(location: MapLocationMarker?) = Unit

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                screenCallbacks?.requestViewportCommand(command, ScreenCallbacks.ViewportCommandSource.LOCATION)
            }

            override fun onLocationRequestIssued(request: MapLocationRequest) {
                screenCallbacks?.onLocationRequestIssued()
            }
        },
    )

    private val screenComponent = DefaultMapScreenComponent(
        componentContext = childContext(key = SCREEN_CHILD_CONTEXT_KEY),
        initialModel = initialModel,
        mapRouterStoreFactory = mapRouterStoreFactory,
        toolsComponent = toolsComponent,
        drawingComponent = drawingComponent,
        locationComponent = locationComponent,
        rulerComponent = rulerComponent,
        viewportComponent = viewportComponent,
    ).also { screen ->
        screenCallbacks = ScreenCallbacks(screen)
    }

    override val model: Value<MapScreenComponent.Model> = screenComponent.model

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = screenComponent.onCameraIdle(snapshot)

    override fun onMapToolsClick() = screenComponent.onMapToolsClick()

    override fun onMapToolsDismiss() = screenComponent.onMapToolsDismiss()

    override fun onZoomInClick() = screenComponent.onZoomInClick()

    override fun onZoomOutClick() = screenComponent.onZoomOutClick()

    override fun onAvailableMapsClick() = screenComponent.onAvailableMapsClick()

    override fun onAvailableMapsDismiss() = screenComponent.onAvailableMapsDismiss()

    override fun onAvailableMapSelect(mapId: String) = screenComponent.onAvailableMapSelect(mapId)

    override fun onAvailableMapConfirm() = screenComponent.onAvailableMapConfirm()

    override fun onAvailableMapSelectionDismiss() = screenComponent.onAvailableMapSelectionDismiss()

    override fun onMapsOnScreenClick() = screenComponent.onMapsOnScreenClick()

    override fun onMapsOnScreenDismiss() = screenComponent.onMapsOnScreenDismiss()

    override fun onMapLayerActionsClick(layerId: String) = screenComponent.onMapLayerActionsClick(layerId)

    override fun onMapLayerActionsDismiss() = screenComponent.onMapLayerActionsDismiss()

    override fun onMoveLayerUpClick() = screenComponent.onMoveLayerUpClick()

    override fun onMoveLayerDownClick() = screenComponent.onMoveLayerDownClick()

    override fun onRemoveLayerClick() = screenComponent.onRemoveLayerClick()

    override fun onLayerOpacityClick() = screenComponent.onLayerOpacityClick()

    override fun onLayerOpacityChange(value: Float) = screenComponent.onLayerOpacityChange(value)

    override fun onLayerOpacityDismiss() = screenComponent.onLayerOpacityDismiss()

    override fun onGpsToggle() = screenComponent.onGpsToggle()

    override fun onMyLocationClick() = screenComponent.onMyLocationClick()

    override fun onCurrentLocationFocusClick() = screenComponent.onCurrentLocationFocusClick()

    override fun onLocationRequestConsumed() = screenComponent.onLocationRequestConsumed()

    override fun onLocationResult(result: LocationRequestResult) = screenComponent.onLocationResult(result)

    override fun onRulerToggle() = screenComponent.onRulerToggle()

    override fun onViewportCommandConsumed() = screenComponent.onViewportCommandConsumed()

    override fun onCenterMarkerClick() = screenComponent.onCenterMarkerClick()

    override fun onCenterMarkerMenuDismiss() = screenComponent.onCenterMarkerMenuDismiss()

    override fun onCreatePointClick() = screenComponent.onCreatePointClick()

    override fun onCreateLineClick() = screenComponent.onCreateLineClick()

    override fun onCreatePolygonClick() = screenComponent.onCreatePolygonClick()

    override fun onCreatePointLatitudeChange(value: String) = screenComponent.onCreatePointLatitudeChange(value)

    override fun onCreatePointLongitudeChange(value: String) = screenComponent.onCreatePointLongitudeChange(value)

    override fun onCreatePointTitleChange(value: String) = screenComponent.onCreatePointTitleChange(value)

    override fun onCreatePointConfirm() = screenComponent.onCreatePointConfirm()

    override fun onCreatePointSheetDismiss() = screenComponent.onCreatePointSheetDismiss()

    override fun onDrawingAddPositionClick() = screenComponent.onDrawingAddPositionClick()

    override fun onDrawingRemoveLastPositionClick() = screenComponent.onDrawingRemoveLastPositionClick()

    override fun onDrawingDetailsClick() = screenComponent.onDrawingDetailsClick()

    override fun onDrawingDismiss() = screenComponent.onDrawingDismiss()

    override fun onCreateShapeTitleChange(value: String) = screenComponent.onCreateShapeTitleChange(value)

    override fun onCreateShapeConfirm() = screenComponent.onCreateShapeConfirm()

    override fun onCreateShapeSheetDismiss() = screenComponent.onCreateShapeSheetDismiss()

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) = screenComponent.onFeatureClick(featureKey, featureType, anchor)

    override fun onFeatureInfoWindowDismiss() = screenComponent.onFeatureInfoWindowDismiss()

    private companion object {
        const val SCREEN_CHILD_CONTEXT_KEY = "map_screen"
        const val TOOLS_CHILD_CONTEXT_KEY = "tools"
        const val DRAWING_CHILD_CONTEXT_KEY = "drawing"
        const val LOCATION_CHILD_CONTEXT_KEY = "location"
        const val RULER_CHILD_CONTEXT_KEY = "ruler"
        const val VIEWPORT_CHILD_CONTEXT_KEY = "viewport"
    }

    private class ScreenCallbacks(
        private val screenComponent: DefaultMapScreenComponent,
    ) {
        fun onToolsStateChanged() {
            screenComponent.onToolsStateChanged()
        }

        fun onDrawingStateChanged() {
            screenComponent.onDrawingStateChanged()
        }

        fun onLocationStateChanged() {
            screenComponent.onLocationStateChanged()
        }

        fun onLocationRequestIssued() {
            screenComponent.onLocationRequestIssued()
        }

        fun onRulerStateChanged() {
            screenComponent.onRulerStateChanged()
        }

        fun onViewportStateChanged() {
            screenComponent.onViewportStateChanged()
        }

        fun requestViewportCommand(command: MapViewportCommand, source: ViewportCommandSource) {
            screenComponent.requestViewportCommand(
                source = when (source) {
                    ViewportCommandSource.LOCATION -> MapRouterStore.ViewportCommandSource.LOCATION
                    ViewportCommandSource.RULER -> MapRouterStore.ViewportCommandSource.RULER
                    ViewportCommandSource.VIEWPORT -> MapRouterStore.ViewportCommandSource.VIEWPORT
                },
                command = command,
            )
        }

        enum class ViewportCommandSource {
            LOCATION,
            RULER,
            VIEWPORT,
        }
    }
}
