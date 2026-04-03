package ru.tech.demomapapp.feature.map.viewport

import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `pending command cleared removes command`() {
        val newState = ViewportReducer.reduce(
            ViewportStore.State(pendingCommand = MapViewportCommand.ZoomIn),
            ViewportStore.Message.PendingCommandUpdated(command = null),
        )

        assertEquals(null, newState.pendingCommand)
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
