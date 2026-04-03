package ru.tech.demomapapp.feature.map.host

import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.impl.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.impl.location.LocationComponent
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStore
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreHolder
import ru.tech.demomapapp.feature.map.impl.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.impl.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.impl.viewport.ViewportComponent

internal class MapScreenRouterBridge(
    private val routerHolder: MapRouterStoreHolder,
    private val toolsComponent: ToolsComponent,
    private val drawingComponent: DrawingComponent,
    private val locationComponent: LocationComponent,
    private val rulerComponent: RulerComponent,
    private val viewportComponent: ViewportComponent,
) {
    private var syncedRulerLocation = locationComponent.model.value.currentMarker
    private var syncedRulerSnapshot = viewportComponent.model.value.cameraSnapshot

    fun syncAllStates() {
        syncToolsState()
        syncDrawingState()
        syncLocationState()
        syncRulerState()
        syncViewportState()
        syncCenterMarkerState()
        syncRulerInputs()
    }

    fun syncToolsState() {
        routerHolder.accept(MapRouterStore.Intent.ToolsStateUpdated(toolsComponent.model.value.toRouterState()))
    }

    fun syncDrawingState() {
        routerHolder.accept(MapRouterStore.Intent.DrawingStateUpdated(drawingComponent.model.value.toRouterState()))
    }

    fun syncLocationState() {
        routerHolder.accept(MapRouterStore.Intent.LocationStateUpdated(locationComponent.model.value.toRouterState()))
    }

    fun syncRulerState() {
        routerHolder.accept(MapRouterStore.Intent.RulerStateUpdated(rulerComponent.model.value.toRouterState()))
    }

    fun syncViewportState() {
        routerHolder.accept(
            MapRouterStore.Intent.ViewportStateUpdated(viewportComponent.model.value.toViewportRouterState()),
        )
    }

    fun syncCenterMarkerState() {
        routerHolder.accept(
            MapRouterStore.Intent.CenterMarkerStateUpdated(viewportComponent.model.value.toCenterMarkerRouterState()),
        )
    }

    fun syncRulerInputs() {
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

    fun onLocationStateChanged() {
        syncLocationState()
        syncRulerInputs()
    }

    fun onToolsStateChanged() {
        syncToolsState()
    }

    fun onDrawingStateChanged() {
        syncDrawingState()
    }

    fun onRulerStateChanged() {
        syncRulerState()
    }

    fun onViewportStateChanged() {
        syncViewportState()
        syncCenterMarkerState()
        syncRulerInputs()
    }

    fun onLocationRequestIssued() {
        syncLocationState()
    }

    fun requestViewportCommand(source: MapRouterStore.ViewportCommandSource, command: MapViewportCommand) {
        routerHolder.accept(MapRouterStore.Intent.ViewportCommandUpdated(source = source, command = command))
    }

    fun consumeViewportCommand() {
        when (currentViewportCommandSource()) {
            MapRouterStore.ViewportCommandSource.VIEWPORT -> {
                viewportComponent.onViewportCommandConsumed()
                routerHolder.accept(
                    MapRouterStore.Intent.ViewportCommandConsumed(MapRouterStore.ViewportCommandSource.VIEWPORT),
                )
            }

            MapRouterStore.ViewportCommandSource.LOCATION -> {
                routerHolder.accept(
                    MapRouterStore.Intent.ViewportCommandConsumed(MapRouterStore.ViewportCommandSource.LOCATION),
                )
            }

            MapRouterStore.ViewportCommandSource.RULER -> {
                routerHolder.accept(
                    MapRouterStore.Intent.ViewportCommandConsumed(MapRouterStore.ViewportCommandSource.RULER),
                )
            }

            null -> Unit
        }
        syncViewportState()
    }

    fun dismissToolsMenuIfVisible() {
        if (toolsComponent.model.value.isMenuVisible) {
            toolsComponent.onMapToolsDismiss()
            syncToolsState()
        }
    }

    fun dismissViewportMenuIfVisible() {
        if (viewportComponent.model.value.isCenterMarkerMenuVisible) {
            viewportComponent.onCenterMarkerMenuDismiss()
            syncCenterMarkerState()
        }
    }

    fun dismissFeatureInfoWindowIfVisible() {
        if (routerHolder.model.value.selectedFeatureInfoWindow != null) {
            routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)
        }
    }

    private fun currentViewportCommandSource(): MapRouterStore.ViewportCommandSource? = when {
        routerHolder.state.viewportPendingCommand != null -> MapRouterStore.ViewportCommandSource.VIEWPORT
        routerHolder.state.locationPendingViewportCommand != null -> MapRouterStore.ViewportCommandSource.LOCATION
        routerHolder.state.rulerPendingViewportCommand != null -> MapRouterStore.ViewportCommandSource.RULER
        else -> null
    }
}
