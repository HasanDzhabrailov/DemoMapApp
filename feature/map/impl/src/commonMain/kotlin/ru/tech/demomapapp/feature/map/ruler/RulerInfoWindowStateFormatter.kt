package ru.tech.demomapapp.feature.map.ruler

import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal fun interface RulerInfoWindowStateFormatter {
    fun format(measurement: RulerMeasurement): RulerInfoWindowState
}

internal object DefaultRulerInfoWindowStateFormatter : RulerInfoWindowStateFormatter {
    override fun format(measurement: RulerMeasurement): RulerInfoWindowState = RulerInfoWindowState(
        distanceText = formatDistance(measurement.distanceMeters),
        trueAzimuthText = "A = ${formatAngleDms(measurement.trueAzimuthDegrees)}",
    )

    private fun formatDistance(distanceMeters: Double): String {
        if (distanceMeters >= 1_000.0) {
            val roundedKilometers = (distanceMeters / 100.0).roundToIntSafely() / 10.0
            return "${roundedKilometers.toString().replace('.', ',')} км"
        }

        return "${distanceMeters.roundToIntSafely()} м"
    }

    private fun formatAngleDms(angleDegrees: Double): String {
        val normalized = normalizeDegrees(angleDegrees)
        val totalSeconds = (normalized * 3600).roundToIntSafely()
        val degrees = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return buildString {
            append(degrees)
            append("° ")
            append(minutes.toTwoDigits())
            append("' ")
            append(seconds.toTwoDigits())
            append('"')
        }
    }
}

private fun Int.toTwoDigits(): String = if (this < 10) "0$this" else toString()
