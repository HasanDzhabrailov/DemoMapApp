package ru.tech.demomapapp.feature.map.ruler

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import ru.tech.demomapapp.feature.map.api.RulerMeasurement
import ru.tech.demomapapp.feature.map.render.RenderRulerArrowSegment

internal fun interface RulerArrowGeometryCalculator {
    fun calculate(measurement: RulerMeasurement): List<RenderRulerArrowSegment>
}

internal object DefaultRulerArrowGeometryCalculator : RulerArrowGeometryCalculator {
    private const val EarthRadiusMeters = 6_371_000.0
    private const val ArrowAngleDegrees = 22.0
    private const val ArrowMinLengthMeters = 180.0
    private const val ArrowMaxLengthMeters = 560.0
    private const val ArrowLengthRatio = 0.085

    override fun calculate(measurement: RulerMeasurement): List<RenderRulerArrowSegment> {
        if (measurement.distanceMeters < 1.0) {
            return emptyList()
        }

        val arrowLengthMeters = (measurement.distanceMeters * ArrowLengthRatio)
            .coerceIn(ArrowMinLengthMeters, ArrowMaxLengthMeters)
        val leftPoint = destinationPoint(
            latitude = measurement.endLatitude,
            longitude = measurement.endLongitude,
            distanceMeters = arrowLengthMeters,
            bearingDegrees = measurement.trueAzimuthDegrees + 180.0 - ArrowAngleDegrees,
        )
        val rightPoint = destinationPoint(
            latitude = measurement.endLatitude,
            longitude = measurement.endLongitude,
            distanceMeters = arrowLengthMeters,
            bearingDegrees = measurement.trueAzimuthDegrees + 180.0 + ArrowAngleDegrees,
        )

        return listOf(
            RenderRulerArrowSegment(
                startLatitude = leftPoint.latitude,
                startLongitude = leftPoint.longitude,
                endLatitude = measurement.endLatitude,
                endLongitude = measurement.endLongitude,
            ),
            RenderRulerArrowSegment(
                startLatitude = rightPoint.latitude,
                startLongitude = rightPoint.longitude,
                endLatitude = measurement.endLatitude,
                endLongitude = measurement.endLongitude,
            ),
        )
    }

    private fun destinationPoint(
        latitude: Double,
        longitude: Double,
        distanceMeters: Double,
        bearingDegrees: Double,
    ): GeoPoint {
        val angularDistance = distanceMeters / EarthRadiusMeters
        val bearingRadians = bearingDegrees.toRadians()
        val latitudeRadians = latitude.toRadians()
        val longitudeRadians = longitude.toRadians()

        val endLatitudeRadians = asin(
            sin(latitudeRadians) * cos(angularDistance) +
                cos(latitudeRadians) * sin(angularDistance) * cos(bearingRadians),
        )
        val endLongitudeRadians = longitudeRadians + atan2(
            sin(bearingRadians) * sin(angularDistance) * cos(latitudeRadians),
            cos(angularDistance) - sin(latitudeRadians) * sin(endLatitudeRadians),
        )

        return GeoPoint(
            latitude = endLatitudeRadians.toDegrees(),
            longitude = normalizeLongitude(endLongitudeRadians.toDegrees()),
        )
    }
}

private data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)

private fun normalizeLongitude(value: Double): Double {
    val normalized = ((value + 540.0) % 360.0) - 180.0
    return when {
        normalized > 180.0 -> 180.0
        normalized < -180.0 -> -180.0
        else -> normalized
    }
}
