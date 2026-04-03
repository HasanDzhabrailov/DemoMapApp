package ru.tech.demomapapp.feature.map.host

import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.mapscreen.DefaultMapScreenComponent
import ru.tech.demomapapp.feature.map.mapscreen.toCenterMarkerRouterState
import ru.tech.demomapapp.feature.map.mapscreen.toRouterState
import ru.tech.demomapapp.feature.map.mapscreen.toViewportRouterState
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.viewport.ViewportComponent

internal class MapHostRouterBridge(
    private val screenComponent: DefaultMapScreenComponent,
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
        screenComponent.onToolsStateUpdated(toolsComponent.model.value.toRouterState())
    }

    fun syncDrawingState() {
        screenComponent.onDrawingStateUpdated(drawingComponent.model.value.toRouterState())
    }

    fun syncLocationState() {
        screenComponent.onLocationStateUpdated(locationComponent.model.value.toRouterState())
    }

    fun syncRulerState() {
        screenComponent.onRulerStateUpdated(rulerComponent.model.value.toRouterState())
    }

    fun syncViewportState() {
        screenComponent.onViewportStateUpdated(viewportComponent.model.value.toViewportRouterState())
    }

    fun syncCenterMarkerState() {
        screenComponent.onCenterMarkerStateUpdated(viewportComponent.model.value.toCenterMarkerRouterState())
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

}
