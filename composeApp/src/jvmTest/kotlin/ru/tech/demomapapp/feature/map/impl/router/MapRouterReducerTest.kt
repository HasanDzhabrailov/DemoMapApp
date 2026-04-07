package ru.tech.demomapapp.feature.map.impl.router

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

class MapRouterReducerTest {

    private val reducer = MapRouterReducer()

    @Test
    fun `overlay interaction clears selected feature info window when configured`() {
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
    fun `overlay interaction preserves selected feature info window when not configured to clear`() {
        val infoWindow = MapScreenComponent.FeatureInfoWindow(
            title = "Point",
            createdAtText = "now",
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
        )
        val state = MapRouterStore.State(
            selectedFeatureInfoWindow = infoWindow,
        )

        val newState = reduce(
            state,
            MapRouterMessage.OverlayInteractionProcessed(MapRouterStore.OverlayTarget.TOOLS_OVERLAY),
        )

        assertEquals(infoWindow, newState.selectedFeatureInfoWindow)
    }

    @Test
    fun `feature info window updated sets the info window`() {
        val infoWindow = MapScreenComponent.FeatureInfoWindow(
            title = "Point",
            createdAtText = "now",
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
        )

        val newState = reduce(
            MapRouterStore.State(),
            MapRouterMessage.FeatureInfoWindowUpdated(infoWindow),
        )

        assertEquals(infoWindow, newState.selectedFeatureInfoWindow)
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
        // Viewport command has priority - it clears other pending commands
        val withRuler = reduce(
            MapRouterStore.State(
                locationPendingViewportCommand = MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
            ),
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

        // Viewport command clears location and ruler commands
        assertEquals(MapRouterStore.ViewportCommandSource.VIEWPORT, withViewport.currentViewportCommandSource)
        assertEquals(MapViewportCommand.ZoomIn, withViewport.pendingViewportCommand)

        // After consuming viewport command, no commands left (location and ruler were cleared)
        val consumedViewport = reduce(withViewport, MapRouterMessage.ViewportCommandConsumed)
        assertEquals(null, consumedViewport.pendingViewportCommand)
        assertEquals(null, consumedViewport.currentViewportCommandSource)
    }

    @Test
    fun `ruler enabled updated sets the flag`() {
        val newState = reduce(
            MapRouterStore.State(),
            MapRouterMessage.RulerEnabledUpdated(enabled = true),
        )

        assertEquals(true, newState.isRulerEnabled)
    }

    private fun reduce(state: MapRouterStore.State, message: MapRouterMessage): MapRouterStore.State =
        with(reducer) { state.reduce(message) }
}