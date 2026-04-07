package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapRouterExecutorTest {

    @Test
    fun `tools overlay interaction publishes overlay processed message`() {
        val executor = MapRouterExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapRouterStore.State(),
        )

        executor.init(callbacks)
        executor.executeIntent(
            MapRouterStore.Intent.OverlayInteractionRequested(MapRouterStore.OverlayTarget.TOOLS_OVERLAY),
        )

        assertEquals(1, callbacks.messages.size)
        assertEquals(
            MapRouterMessage.OverlayInteractionProcessed(MapRouterStore.OverlayTarget.TOOLS_OVERLAY),
            callbacks.messages[0],
        )
        // TOOLS_OVERLAY has dismissViewportMenu = true
        assertEquals(1, callbacks.labels.size)
        assertEquals(MapRouterStore.Label.DismissViewportMenu, callbacks.labels[0])
    }

    @Test
    fun `drawing overlay interaction dismisses both conflicting menus`() {
        val executor = MapRouterExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapRouterStore.State(),
        )

        executor.init(callbacks)
        executor.executeIntent(
            MapRouterStore.Intent.OverlayInteractionRequested(MapRouterStore.OverlayTarget.DRAWING_OVERLAY),
        )

        assertEquals(1, callbacks.messages.size)
        assertEquals(
            MapRouterMessage.OverlayInteractionProcessed(MapRouterStore.OverlayTarget.DRAWING_OVERLAY),
            callbacks.messages[0],
        )
        assertEquals(2, callbacks.labels.size)
        assertTrue(callbacks.labels.contains(MapRouterStore.Label.DismissToolsMenu))
        assertTrue(callbacks.labels.contains(MapRouterStore.Label.DismissViewportMenu))
    }

    @Test
    fun `center marker click requests menu open`() {
        val executor = MapRouterExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapRouterStore.State(),
        )

        executor.init(callbacks)
        executor.executeIntent(MapRouterStore.Intent.CenterMarkerClicked)

        assertEquals(1, callbacks.labels.size)
        assertEquals(MapRouterStore.Label.CenterMarkerMenuOpenRequested, callbacks.labels[0])
    }

    private class TestExecutorCallbacks(
        override var state: MapRouterStore.State,
    ) : Executor.Callbacks<MapRouterStore.State, MapRouterMessage, MapRouterStore.Action, MapRouterStore.Label> {
        val messages = mutableListOf<MapRouterMessage>()
        val labels = mutableListOf<MapRouterStore.Label>()

        override fun onMessage(message: MapRouterMessage) {
            messages += message
        }

        override fun onAction(action: MapRouterStore.Action) = Unit

        override fun onLabel(label: MapRouterStore.Label) {
            labels += label
        }
    }
}