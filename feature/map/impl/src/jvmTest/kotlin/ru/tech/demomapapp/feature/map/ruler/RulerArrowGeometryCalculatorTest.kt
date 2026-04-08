package ru.tech.demomapapp.feature.map.ruler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

class RulerArrowGeometryCalculatorTest {

    @Test
    fun `arrow geometry returns two segments ending at ruler end point`() {
        val measurement = RulerMeasurement(
            startLatitude = 55.75,
            startLongitude = 37.61,
            endLatitude = 55.85,
            endLongitude = 37.71,
            distanceMeters = 12_900.0,
            trueAzimuthDegrees = 97.5580555556,
        )

        val segments = DefaultRulerArrowGeometryCalculator.calculate(measurement)

        assertEquals(2, segments.size)
        assertTrue(segments.all { it.endLatitude == measurement.endLatitude })
        assertTrue(segments.all { it.endLongitude == measurement.endLongitude })
        assertTrue(segments.all { it.startLatitude != it.endLatitude || it.startLongitude != it.endLongitude })
    }

    @Test
    fun `arrow geometry is empty for zero length ruler`() {
        val measurement = RulerMeasurement(
            startLatitude = 55.75,
            startLongitude = 37.61,
            endLatitude = 55.75,
            endLongitude = 37.61,
            distanceMeters = 0.0,
            trueAzimuthDegrees = 0.0,
        )

        val segments = DefaultRulerArrowGeometryCalculator.calculate(measurement)

        assertTrue(segments.isEmpty())
    }
}
