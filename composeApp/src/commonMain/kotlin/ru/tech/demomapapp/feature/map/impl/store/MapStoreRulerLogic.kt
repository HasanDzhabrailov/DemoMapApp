package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator

internal data class MapStoreRulerResolution(
    val fallbackMarker: MapLocationMarker? = null,
    val measurement: RulerMeasurement? = null,
    val infoWindow: RulerInfoWindowState? = null,
)

internal fun MapStore.State.resolveRulerState(
    rulerMeasurementCalculator: RulerMeasurementCalculator,
    rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter,
): MapStoreRulerResolution {
    if (!isRulerEnabled) {
        return MapStoreRulerResolution()
    }

    val snapshot = lastCameraSnapshot ?: return MapStoreRulerResolution()
    val resolvedMarker = currentLocationMarker ?: MapLocationMarker(
        latitude = snapshot.latitude,
        longitude = snapshot.longitude,
        isPlaceholder = true,
    )
    val measurement = rulerMeasurementCalculator.calculate(
        startLatitude = resolvedMarker.latitude,
        startLongitude = resolvedMarker.longitude,
        endLatitude = snapshot.latitude,
        endLongitude = snapshot.longitude,
    )

    return MapStoreRulerResolution(
        fallbackMarker = if (currentLocationMarker == null) resolvedMarker else null,
        measurement = measurement,
        infoWindow = rulerInfoWindowStateFormatter.format(measurement),
    )
}
