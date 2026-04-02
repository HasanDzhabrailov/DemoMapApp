package ru.tech.demomapapp.feature.map.impl.location

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode

class DefaultLocationComponentTest {

    @Test
    fun `component forwards store labels to output callbacks`() {
        val output = TestOutput()
        val component = DefaultLocationComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            locationStoreFactory = LocationStoreFactory(),
            output = output,
        )

        component.onCameraSnapshotReceived(
            MapCameraSnapshot(latitude = 59.0, longitude = 30.0, zoom = 11.0, bearing = 0.0),
        )
        component.onMyLocationClick()

        assertEquals(MyLocationMode.MANUAL_PLACEHOLDER, component.model.value.mode)
        assertEquals(
            MapLocationMarker(latitude = 59.0, longitude = 30.0, isPlaceholder = true),
            output.locations.single(),
        )
        assertTrue(output.requests.isEmpty())

        component.onGpsToggle()

        assertEquals(MyLocationMode.OFF, component.model.value.mode)
        assertNull(component.model.value.currentMarker)
        assertEquals(MapLocationRequest.EnableGpsLocationRequest, component.model.value.pendingRequest)
        assertEquals(null, output.locations.last())
        assertEquals(MapLocationRequest.EnableGpsLocationRequest, output.requests.last())

        component.onLocationRequestConsumed()
        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))

        assertEquals(MyLocationMode.GPS, component.model.value.mode)
        assertEquals(MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6), output.viewportCommands.last())
        assertEquals(
            MapLocationMarker(latitude = 55.7, longitude = 37.6, isPlaceholder = false),
            output.locations.last(),
        )
    }

    private class TestOutput : LocationComponent.Output {
        val locations = mutableListOf<MapLocationMarker?>()
        val viewportCommands = mutableListOf<MapViewportCommand>()
        val requests = mutableListOf<MapLocationRequest>()

        override fun onLocationUpdated(location: MapLocationMarker?) {
            locations += location
        }

        override fun onViewportCommandRequested(command: MapViewportCommand) {
            viewportCommands += command
        }

        override fun onLocationRequestIssued(request: MapLocationRequest) {
            requests += request
        }
    }
}
