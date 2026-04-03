package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Executor
import kotlin.test.Test
import kotlin.test.assertEquals

class MapRouterExecutorTest {

    @Test
    fun `tools overlay interaction dismisses viewport menu when visible`() {
        val executor = MapRouterExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapRouterStore.State(
                centerMarkerState = MapRouterStore.ChildState.CenterMarker(isMenuVisible = true),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(
            MapRouterStore.Intent.OverlayInteractionRequested(MapRouterStore.OverlayTarget.TOOLS_OVERLAY),
        )

        assertEquals(
            listOf(
                MapRouterMessage.OverlayInteractionProcessed(MapRouterStore.OverlayTarget.TOOLS_OVERLAY),
            ),
            callbacks.messages,
        )
        assertEquals(
            listOf<MapRouterStore.Label>(MapRouterStore.Label.DismissViewportMenu),
            callbacks.labels,
        )
    }

    @Test
    fun `drawing overlay interaction dismisses both conflicting menus`() {
        val executor = MapRouterExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapRouterStore.State(
                toolsState = MapRouterStore.ChildState.Tools(isMapToolsMenuVisible = true),
                centerMarkerState = MapRouterStore.ChildState.CenterMarker(isMenuVisible = true),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(
            MapRouterStore.Intent.OverlayInteractionRequested(MapRouterStore.OverlayTarget.DRAWING_OVERLAY),
        )

        assertEquals(
            listOf(
                MapRouterMessage.OverlayInteractionProcessed(MapRouterStore.OverlayTarget.DRAWING_OVERLAY),
            ),
            callbacks.messages,
        )
        assertEquals(
            listOf<MapRouterStore.Label>(
                MapRouterStore.Label.DismissToolsMenu,
                MapRouterStore.Label.DismissViewportMenu,
            ),
            callbacks.labels,
        )
    }

    private class TestExecutorCallbacks(
        override var state: MapRouterStore.State,
    ) : Executor.Callbacks<MapRouterStore.State, MapRouterMessage, Nothing, MapRouterStore.Label> {
        val messages = mutableListOf<MapRouterMessage>()
        val labels = mutableListOf<MapRouterStore.Label>()

        override fun onMessage(message: MapRouterMessage) {
            messages += message
        }

        override fun onAction(action: Nothing) = Unit

        override fun onLabel(label: MapRouterStore.Label) {
            labels += label
        }
    }
}
