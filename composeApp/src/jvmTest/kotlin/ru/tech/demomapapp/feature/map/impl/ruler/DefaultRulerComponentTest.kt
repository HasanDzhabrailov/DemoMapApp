package ru.tech.demomapapp.feature.map.impl.ruler

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

class DefaultRulerComponentTest {

    @Test
    fun `toggle enables and disables ruler state`() {
        val component = createComponent()

        component.onCameraSnapshotReceived(defaultSnapshot())
        component.onToggleClicked()

        assertTrue(component.model.value.isEnabled)
        assertNotNull(component.model.value.measurement)
        assertNotNull(component.model.value.infoWindow)

        component.onToggleClicked()

        assertFalse(component.model.value.isEnabled)
        assertNull(component.model.value.measurement)
        assertNull(component.model.value.infoWindow)
    }

    @Test
    fun `location update recalculates measurement while enabled`() {
        val component = createComponent()

        component.onCameraSnapshotReceived(defaultSnapshot(latitude = 59.1, longitude = 30.1))
        component.onToggleClicked()
        component.onLocationUpdated(
            MapLocationMarker(
                latitude = 59.0,
                longitude = 30.0,
                isPlaceholder = false,
            ),
        )

        val measurement = assertNotNull(component.model.value.measurement)
        assertEquals(59.0, measurement.startLatitude)
        assertEquals(30.0, measurement.startLongitude)
        assertEquals(59.1, measurement.endLatitude)
        assertEquals(30.1, measurement.endLongitude)
    }

    @Test
    fun `enable emits viewport output when snapshot exists`() {
        var emittedCommand: MapViewportCommand? = null
        val component = createComponent(
            output = object : RulerComponent.Output {
                override fun onStateChanged() = Unit

                override fun onViewportCommandRequested(command: MapViewportCommand) {
                    emittedCommand = command
                }
            },
        )

        component.onCameraSnapshotReceived(defaultSnapshot(latitude = 55.7, longitude = 37.6))
        component.onToggleClicked()

        assertEquals(
            MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
            emittedCommand,
        )
    }

    private fun createComponent(
        output: RulerComponent.Output = object : RulerComponent.Output {
            override fun onStateChanged() = Unit

            override fun onViewportCommandRequested(command: MapViewportCommand) = Unit
        },
    ): DefaultRulerComponent {
        val lifecycle = LifecycleRegistry()
        return DefaultRulerComponent(
            componentContext = DefaultComponentContext(lifecycle),
            rulerStoreFactory = RulerStoreFactory(),
            output = output,
        )
    }

    private fun defaultSnapshot(latitude: Double = 59.0, longitude: Double = 30.0): MapCameraSnapshot {
        return MapCameraSnapshot(
            latitude = latitude,
            longitude = longitude,
            zoom = 10.0,
            bearing = 0.0,
        )
    }
}
