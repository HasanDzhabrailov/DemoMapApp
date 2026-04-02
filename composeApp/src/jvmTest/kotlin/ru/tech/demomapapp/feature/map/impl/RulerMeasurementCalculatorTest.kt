package ru.tech.demomapapp.feature.map.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RulerMeasurementCalculatorTest {

    @Test
    fun `calculator returns zero distance for identical points`() {
        val measurement = DefaultRulerMeasurementCalculator.calculate(
            startLatitude = 55.75,
            startLongitude = 37.61,
            endLatitude = 55.75,
            endLongitude = 37.61,
        )

        assertEquals(0.0, measurement.distanceMeters)
        assertEquals(0.0, measurement.trueAzimuthDegrees)
    }

    @Test
    fun `calculator returns eastward azimuth for equator east movement`() {
        val measurement = DefaultRulerMeasurementCalculator.calculate(
            startLatitude = 0.0,
            startLongitude = 0.0,
            endLatitude = 0.0,
            endLongitude = 1.0,
        )

        assertTrue(measurement.distanceMeters in 111000.0..112000.0)
        assertTrue(measurement.trueAzimuthDegrees in 89.9..90.1)
    }

    @Test
    fun `formatter returns localized distance and dms azimuth text`() {
        val infoWindow = DefaultRulerInfoWindowStateFormatter.format(
            measurement = DefaultRulerMeasurementCalculator.calculate(
                startLatitude = 0.0,
                startLongitude = 0.0,
                endLatitude = 0.0,
                endLongitude = 0.11598,
            ).copy(trueAzimuthDegrees = 97.5580555556),
        )

        assertEquals("12,9 км", infoWindow.distanceText)
        assertEquals("A = 97° 33' 29\"", infoWindow.trueAzimuthText)
    }
}
