package ru.tech.demomapapp.feature.map.impl.viewport

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

class DefaultViewportComponentTest {

    @Test
    fun `zoom in emits viewport command through output and model`() {
        var emittedCommand: MapViewportCommand? = null
        val component = createComponent(
            output = ViewportComponent.Output { command -> emittedCommand = command },
        )

        component.onZoomInClick()

        assertEquals(MapViewportCommand.ZoomIn, emittedCommand)
        assertEquals(MapViewportCommand.ZoomIn, component.model.value.pendingCommand)
    }

    @Test
    fun `zoom out emits viewport command through output and model`() {
        var emittedCommand: MapViewportCommand? = null
        val component = createComponent(
            output = ViewportComponent.Output { command -> emittedCommand = command },
        )

        component.onZoomOutClick()

        assertEquals(MapViewportCommand.ZoomOut, emittedCommand)
        assertEquals(MapViewportCommand.ZoomOut, component.model.value.pendingCommand)
    }

    @Test
    fun `camera idle stores snapshot`() {
        val component = createComponent()
        val snapshot = defaultSnapshot(latitude = 55.7, longitude = 37.6, zoom = 13.0)

        component.onCameraIdle(snapshot)

        assertEquals(snapshot, component.model.value.cameraSnapshot)
    }

    @Test
    fun `center marker menu opens and closes`() {
        val component = createComponent()

        component.onCenterMarkerClick()
        assertTrue(component.model.value.isCenterMarkerMenuVisible)

        component.onCenterMarkerMenuDismiss()
        assertFalse(component.model.value.isCenterMarkerMenuVisible)
        assertNull(component.model.value.pendingCommand)
    }

    private fun createComponent(
        output: ViewportComponent.Output = ViewportComponent.Output { _ -> },
    ): DefaultViewportComponent {
        return DefaultViewportComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            viewportStoreFactory = ViewportStoreFactory(),
            output = output,
        )
    }

    private fun defaultSnapshot(
        latitude: Double = 59.0,
        longitude: Double = 30.0,
        zoom: Double = 10.0,
    ): MapCameraSnapshot {
        return MapCameraSnapshot(
            latitude = latitude,
            longitude = longitude,
            zoom = zoom,
            bearing = 0.0,
        )
    }
}
