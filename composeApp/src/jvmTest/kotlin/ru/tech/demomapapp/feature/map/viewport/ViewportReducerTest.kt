package ru.tech.demomapapp.feature.map.viewport

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

class ViewportReducerTest {

    @Test
    fun `pending command updated stores command`() {
        val newState = ViewportReducer.reduce(
            ViewportStore.State(),
            ViewportStore.Message.PendingCommandUpdated(MapViewportCommand.ZoomIn),
        )

        assertEquals(MapViewportCommand.ZoomIn, newState.pendingCommand)
    }

    @Test
    fun `center marker menu open and dismiss update visibility`() {
        val openedState = ViewportReducer.reduce(
            ViewportStore.State(),
            ViewportStore.Message.CenterMarkerMenuOpened,
        )
        val dismissedState = ViewportReducer.reduce(
            openedState,
            ViewportStore.Message.CenterMarkerMenuDismissed,
        )

        assertTrue(openedState.isCenterMarkerMenuVisible)
        assertFalse(dismissedState.isCenterMarkerMenuVisible)
    }

    @Test
    fun `camera snapshot stored updates viewport snapshot`() {
        val snapshot = MapCameraSnapshot(latitude = 55.75, longitude = 37.61, zoom = 11.0, bearing = 15.0)

        val newState = ViewportReducer.reduce(
            ViewportStore.State(),
            ViewportStore.Message.CameraSnapshotStored(snapshot),
        )

        assertEquals(snapshot, newState.cameraSnapshot)
    }
}
