package ru.tech.demomapapp.feature.map.impl.router

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

class MapRouterReducerTest {

    private val reducer = MapRouterReducer()

    @Test
    fun `tools overlay visibility clears selected feature info window`() {
        val state = MapRouterStore.State(
            selectedFeatureInfoWindow = MapScreenComponent.FeatureInfoWindow(
                title = "Point",
                createdAtText = "now",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val newState = reduce(
            state,
            MapRouterMessage.ToolsStateUpdated(
                MapRouterStore.ChildState.Tools(isMapToolsMenuVisible = true),
            ),
        )

        assertNull(newState.selectedFeatureInfoWindow)
    }

    @Test
    fun `viewport snapshot change clears selected feature info window`() {
        val snapshotA = MapCameraSnapshot(latitude = 55.7, longitude = 37.6, zoom = 10.0, bearing = 0.0)
        val snapshotB = MapCameraSnapshot(latitude = 59.0, longitude = 30.0, zoom = 11.0, bearing = 0.0)
        val state = MapRouterStore.State(
            viewportState = MapRouterStore.ChildState.Viewport(lastCameraSnapshot = snapshotA),
            selectedFeatureInfoWindow = MapScreenComponent.FeatureInfoWindow(
                title = "Point",
                createdAtText = "now",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val newState = reduce(
            state,
            MapRouterMessage.ViewportStateUpdated(
                MapRouterStore.ChildState.Viewport(lastCameraSnapshot = snapshotB),
            ),
        )

        assertNull(newState.selectedFeatureInfoWindow)
    }

    @Test
    fun `center marker interaction clears selected feature info window`() {
        val state = MapRouterStore.State(
            selectedFeatureInfoWindow = MapScreenComponent.FeatureInfoWindow(
                title = "Point",
                createdAtText = "now",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val newState = reduce(
            state,
            MapRouterMessage.OverlayInteractionProcessed(MapRouterStore.OverlayTarget.CENTER_MARKER_MENU),
        )

        assertNull(newState.selectedFeatureInfoWindow)
    }

    @Test
    fun `viewport command priority prefers viewport then location then ruler`() {
        val withLocation = reduce(
            MapRouterStore.State(),
            MapRouterMessage.ViewportCommandUpdated(
                source = MapRouterStore.ViewportCommandSource.LOCATION,
                command = MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
            ),
        )
        val withRuler = reduce(
            withLocation,
            MapRouterMessage.ViewportCommandUpdated(
                source = MapRouterStore.ViewportCommandSource.RULER,
                command = MapViewportCommand.MoveTo(latitude = 59.0, longitude = 30.0),
            ),
        )
        val withViewport = reduce(
            withRuler,
            MapRouterMessage.ViewportCommandUpdated(
                source = MapRouterStore.ViewportCommandSource.VIEWPORT,
                command = MapViewportCommand.ZoomIn,
            ),
        )

        assertEquals(MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6), withLocation.pendingViewportCommand)
        assertEquals(MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6), withRuler.pendingViewportCommand)
        assertEquals(MapViewportCommand.ZoomIn, withViewport.pendingViewportCommand)
    }

    @Test
    fun `viewport command consumed clears current source and exposes queued command`() {
        val state = reduce(
            reduce(
                MapRouterStore.State(
                    locationPendingViewportCommand = MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
                ),
                MapRouterMessage.ViewportCommandUpdated(
                    source = MapRouterStore.ViewportCommandSource.RULER,
                    command = MapViewportCommand.MoveTo(latitude = 59.0, longitude = 30.0),
                ),
            ),
            MapRouterMessage.ViewportCommandUpdated(
                source = MapRouterStore.ViewportCommandSource.VIEWPORT,
                command = MapViewportCommand.ZoomIn,
            ),
        )

        val consumedViewport = reduce(state, MapRouterMessage.ViewportCommandConsumed)
        val consumedLocation = reduce(consumedViewport, MapRouterMessage.ViewportCommandConsumed)

        assertEquals(MapRouterStore.ViewportCommandSource.VIEWPORT, state.currentViewportCommandSource)
        assertEquals(MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6), consumedViewport.pendingViewportCommand)
        assertEquals(MapRouterStore.ViewportCommandSource.LOCATION, consumedViewport.currentViewportCommandSource)
        assertEquals(MapViewportCommand.MoveTo(latitude = 59.0, longitude = 30.0), consumedLocation.pendingViewportCommand)
        assertEquals(MapRouterStore.ViewportCommandSource.RULER, consumedLocation.currentViewportCommandSource)
    }

    private fun reduce(state: MapRouterStore.State, message: MapRouterMessage): MapRouterStore.State =
        with(reducer) { state.reduce(message) }
}
