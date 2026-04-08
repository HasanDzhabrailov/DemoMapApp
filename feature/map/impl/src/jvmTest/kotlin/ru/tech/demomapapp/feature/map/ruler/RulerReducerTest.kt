package ru.tech.demomapapp.feature.map.ruler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

class RulerReducerTest {

    @Test
    fun `measurement updated stores measurement and info window`() {
        val measurement = RulerMeasurement(
            startLatitude = 55.0,
            startLongitude = 37.0,
            endLatitude = 55.1,
            endLongitude = 37.1,
            distanceMeters = 1200.0,
            trueAzimuthDegrees = 90.0,
        )
        val infoWindow = RulerInfoWindowState(distanceText = "1.2 km", trueAzimuthText = "90°")

        val newState = RulerReducer.reduce(
            RulerStore.State(),
            RulerStore.Message.MeasurementUpdated(measurement = measurement, infoWindow = infoWindow),
        )

        assertEquals(measurement, newState.measurement)
        assertEquals(infoWindow, newState.infoWindow)
    }

    @Test
    fun `disabled ruler clears measurement and info window`() {
        val state = RulerStore.State(
            isEnabled = true,
            measurement = RulerMeasurement(
                startLatitude = 55.0,
                startLongitude = 37.0,
                endLatitude = 55.1,
                endLongitude = 37.1,
                distanceMeters = 1200.0,
                trueAzimuthDegrees = 90.0,
            ),
            infoWindow = RulerInfoWindowState(distanceText = "1.2 km", trueAzimuthText = "90°"),
        )

        val newState = RulerReducer.reduce(state, RulerStore.Message.EnabledUpdated(isEnabled = false))

        assertEquals(false, newState.isEnabled)
        assertNull(newState.measurement)
        assertNull(newState.infoWindow)
    }

    @Test
    fun `location and snapshot updates are stored`() {
        val marker = MapLocationMarker(latitude = 55.75, longitude = 37.61, isPlaceholder = false)
        val snapshot = MapCameraSnapshot(latitude = 59.0, longitude = 30.0, zoom = 10.0, bearing = 0.0)

        val withLocation = RulerReducer.reduce(
            RulerStore.State(),
            RulerStore.Message.LocationStored(marker),
        )
        val withSnapshot = RulerReducer.reduce(
            withLocation,
            RulerStore.Message.CameraSnapshotStored(snapshot),
        )

        assertEquals(marker, withSnapshot.currentLocation)
        assertEquals(snapshot, withSnapshot.lastCameraSnapshot)
    }
}
