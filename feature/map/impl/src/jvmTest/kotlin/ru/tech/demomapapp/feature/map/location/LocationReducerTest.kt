package ru.tech.demomapapp.feature.map.location

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MyLocationMode

class LocationReducerTest {

    @Test
    fun `gps enable requested clears marker and creates one shot request`() {
        val state = LocationStore.State(
            mode = MyLocationMode.MANUAL_PLACEHOLDER,
            currentMarker = MapLocationMarker(latitude = 55.7, longitude = 37.6, isPlaceholder = true),
            hasRealLocation = true,
        )

        val newState = reduce(state, LocationStore.Message.GpsEnableRequested)

        assertEquals(MyLocationMode.OFF, newState.mode)
        assertNull(newState.currentMarker)
        assertEquals(MapLocationRequest.EnableGpsLocationRequest, newState.pendingRequest)
        assertFalse(newState.hasRealLocation)
    }

    @Test
    fun `manual placeholder selected stores placeholder marker`() {
        val marker = MapLocationMarker(latitude = 59.0, longitude = 30.0, isPlaceholder = true)

        val newState = reduce(
            LocationStore.State(),
            LocationStore.Message.ManualPlaceholderSelected(marker),
        )

        assertEquals(MyLocationMode.MANUAL_PLACEHOLDER, newState.mode)
        assertEquals(marker, newState.currentMarker)
        assertNull(newState.pendingRequest)
        assertFalse(newState.hasRealLocation)
    }

    @Test
    fun `location resolved stores gps marker and clears request`() {
        val marker = MapLocationMarker(latitude = 55.75, longitude = 37.61, isPlaceholder = false)

        val newState = reduce(
            LocationStore.State(pendingRequest = MapLocationRequest.EnableGpsLocationRequest),
            LocationStore.Message.LocationResolved(marker),
        )

        assertEquals(MyLocationMode.GPS, newState.mode)
        assertEquals(marker, newState.currentMarker)
        assertNull(newState.pendingRequest)
        assertTrue(newState.hasRealLocation)
    }

    @Test
    fun `camera snapshot stored updates last snapshot`() {
        val snapshot = MapCameraSnapshot(latitude = 55.75, longitude = 37.61, zoom = 12.0, bearing = 0.0)

        val newState = reduce(
            LocationStore.State(),
            LocationStore.Message.CameraSnapshotStored(snapshot),
        )

        assertEquals(snapshot, newState.lastCameraSnapshot)
    }

    private fun reduce(state: LocationStore.State, message: LocationStore.Message): LocationStore.State =
        with(LocationReducer) { state.reduce(message) }
}
