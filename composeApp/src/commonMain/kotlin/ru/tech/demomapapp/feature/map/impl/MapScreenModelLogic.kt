package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MyLocationMode

internal fun recalculateRulerState(
    model: MapScreenComponent.Model,
    rulerMeasurementCalculator: RulerMeasurementCalculator,
    rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter,
): MapScreenComponent.Model {
    if (!model.isRulerEnabled) {
        return clearRulerState(model)
    }

    val snapshot = model.lastCameraSnapshot
    val updatedModel = if (model.currentLocationMarker == null && snapshot != null) {
        model.copy(
            myLocationMode = MyLocationMode.MANUAL_PLACEHOLDER,
            currentLocationMarker = snapshot.toPlaceholderLocationMarker(),
        )
    } else {
        model
    }

    val marker = updatedModel.currentLocationMarker ?: return clearRulerState(updatedModel)
    val endSnapshot = updatedModel.lastCameraSnapshot ?: return clearRulerState(updatedModel)
    val measurement = rulerMeasurementCalculator.calculate(
        startLatitude = marker.latitude,
        startLongitude = marker.longitude,
        endLatitude = endSnapshot.latitude,
        endLongitude = endSnapshot.longitude,
    )
    return updatedModel.copy(
        rulerMeasurement = measurement,
        rulerInfoWindow = rulerInfoWindowStateFormatter.format(measurement),
    )
}

internal fun clearRulerState(model: MapScreenComponent.Model): MapScreenComponent.Model =
    model.copy(
        rulerMeasurement = null,
        rulerInfoWindow = null,
    )

private fun MapCameraSnapshot.toPlaceholderLocationMarker(): MapLocationMarker =
    MapLocationMarker(
        latitude = latitude,
        longitude = longitude,
        isPlaceholder = true,
    )
