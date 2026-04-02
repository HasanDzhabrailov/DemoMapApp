package ru.tech.demomapapp.feature.map.impl.location

import com.arkivanov.mvikotlin.core.store.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode

class LocationExecutorTest {

    @Test
    fun `gps toggle requests enable gps and clears existing manual marker`() {
        val executor = LocationExecutor()
        val callbacks = TestExecutorCallbacks(
            state = LocationStore.State(
                mode = MyLocationMode.MANUAL_PLACEHOLDER,
                currentMarker = MapLocationMarker(latitude = 59.0, longitude = 30.0, isPlaceholder = true),
                lastCameraSnapshot = MapCameraSnapshot(latitude = 59.0, longitude = 30.0, zoom = 10.0, bearing = 0.0),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(LocationStore.Intent.GpsToggled)

        assertEquals(
            listOf<LocationStore.Message>(LocationStore.Message.GpsEnableRequested),
            callbacks.messages,
        )
        assertEquals(
            listOf<LocationStore.Label>(
                LocationStore.Label.LocationUpdated(location = null),
                LocationStore.Label.LocationRequestIssued(MapLocationRequest.EnableGpsLocationRequest),
            ),
            callbacks.labels,
        )
    }

    @Test
    fun `my location click creates manual placeholder from latest camera snapshot`() {
        val executor = LocationExecutor()
        val callbacks = TestExecutorCallbacks(
            state = LocationStore.State(
                lastCameraSnapshot = MapCameraSnapshot(latitude = 55.75, longitude = 37.61, zoom = 12.0, bearing = 0.0),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(LocationStore.Intent.MyLocationClicked)

        val marker = MapLocationMarker(latitude = 55.75, longitude = 37.61, isPlaceholder = true)
        assertEquals(
            listOf<LocationStore.Message>(LocationStore.Message.ManualPlaceholderSelected(marker)),
            callbacks.messages,
        )
        assertEquals(
            listOf<LocationStore.Label>(LocationStore.Label.LocationUpdated(marker)),
            callbacks.labels,
        )
    }

    @Test
    fun `location resolved emits marker update and viewport command`() {
        val executor = LocationExecutor()
        val callbacks = TestExecutorCallbacks(state = LocationStore.State())

        executor.init(callbacks)
        executor.executeIntent(
            LocationStore.Intent.LocationResultReceived(
                LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6),
            ),
        )

        val marker = MapLocationMarker(latitude = 55.7, longitude = 37.6, isPlaceholder = false)
        assertEquals(
            listOf<LocationStore.Message>(LocationStore.Message.LocationResolved(marker)),
            callbacks.messages,
        )
        assertEquals(
            listOf<LocationStore.Label>(
                LocationStore.Label.LocationUpdated(marker),
                LocationStore.Label.ViewportCommandRequested(
                    MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
                ),
            ),
            callbacks.labels,
        )
    }

    @Test
    fun `permission denied clears active gps location`() {
        val executor = LocationExecutor()
        val callbacks = TestExecutorCallbacks(
            state = LocationStore.State(
                mode = MyLocationMode.GPS,
                currentMarker = MapLocationMarker(latitude = 55.7, longitude = 37.6, isPlaceholder = false),
                hasRealLocation = true,
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(
            LocationStore.Intent.LocationResultReceived(LocationRequestResult.PermissionDenied),
        )

        assertEquals(
            listOf<LocationStore.Message>(LocationStore.Message.LocationCleared),
            callbacks.messages,
        )
        assertEquals(
            listOf<LocationStore.Label>(LocationStore.Label.LocationUpdated(location = null)),
            callbacks.labels,
        )
    }

    private class TestExecutorCallbacks(
        override var state: LocationStore.State,
    ) : Executor.Callbacks<LocationStore.State, LocationStore.Message, Nothing, LocationStore.Label> {
        val messages = mutableListOf<LocationStore.Message>()
        val labels = mutableListOf<LocationStore.Label>()

        override fun onMessage(message: LocationStore.Message) {
            messages += message
        }

        override fun onAction(action: Nothing) = Unit

        override fun onLabel(label: LocationStore.Label) {
            labels += label
        }
    }
}
