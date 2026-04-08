package ru.tech.demomapapp.feature.map.ruler

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal fun interface RulerMeasurementCalculator {
    fun calculate(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
    ): RulerMeasurement
}

internal object DefaultRulerMeasurementCalculator : RulerMeasurementCalculator {
    private const val EarthRadiusMeters = 6_371_000.0

    override fun calculate(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
    ): RulerMeasurement = RulerMeasurement(
        startLatitude = startLatitude,
        startLongitude = startLongitude,
        endLatitude = endLatitude,
        endLongitude = endLongitude,
        distanceMeters = haversineDistanceMeters(
            startLatitude = startLatitude,
            startLongitude = startLongitude,
            endLatitude = endLatitude,
            endLongitude = endLongitude,
        ),
        trueAzimuthDegrees = initialBearingDegrees(
            startLatitude = startLatitude,
            startLongitude = startLongitude,
            endLatitude = endLatitude,
            endLongitude = endLongitude,
        ),
    )

    private fun haversineDistanceMeters(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
    ): Double {
        val startLatitudeRadians = startLatitude.toRadians()
        val endLatitudeRadians = endLatitude.toRadians()
        val deltaLatitudeRadians = (endLatitude - startLatitude).toRadians()
        val deltaLongitudeRadians = (endLongitude - startLongitude).toRadians()

        val haversine = sin(deltaLatitudeRadians / 2).square() +
            cos(startLatitudeRadians) * cos(endLatitudeRadians) * sin(deltaLongitudeRadians / 2).square()
        val centralAngle = 2 * asin(sqrt(haversine.coerceIn(0.0, 1.0)))
        return EarthRadiusMeters * centralAngle
    }

    private fun initialBearingDegrees(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
    ): Double {
        if (startLatitude == endLatitude && startLongitude == endLongitude) {
            return 0.0
        }

        val startLatitudeRadians = startLatitude.toRadians()
        val endLatitudeRadians = endLatitude.toRadians()
        val deltaLongitudeRadians = (endLongitude - startLongitude).toRadians()

        val y = sin(deltaLongitudeRadians) * cos(endLatitudeRadians)
        val x = cos(startLatitudeRadians) * sin(endLatitudeRadians) -
            sin(startLatitudeRadians) * cos(endLatitudeRadians) * cos(deltaLongitudeRadians)
        val bearingDegrees = atan2(y, x).toDegrees()
        return normalizeDegrees(bearingDegrees)
    }
}

internal fun Double.toRadians(): Double = this * PI / 180.0

internal fun Double.toDegrees(): Double = this * 180.0 / PI

internal fun Double.square(): Double = this * this

internal fun normalizeDegrees(value: Double): Double {
    val normalized = value % 360.0
    return if (normalized < 0.0) normalized + 360.0 else normalized
}

internal fun Double.roundToIntSafely(): Int = roundToInt()
