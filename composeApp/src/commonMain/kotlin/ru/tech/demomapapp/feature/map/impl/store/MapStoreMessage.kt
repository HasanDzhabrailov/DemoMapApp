package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal sealed interface MapStoreMessage {
    data class StateSynced(val state: MapStore.State) : MapStoreMessage

    data class CameraIdleReceived(val snapshot: MapCameraSnapshot) : MapStoreMessage

    object MapToolsMenuToggled : MapStoreMessage

    object MapToolsMenuDismissed : MapStoreMessage

    object CenterMarkerMenuOpened : MapStoreMessage

    object CenterMarkerMenuDismissed : MapStoreMessage

    object CreatePointSheetOpened : MapStoreMessage

    data class CreatePointLatitudeChanged(val value: String) : MapStoreMessage

    data class CreatePointLongitudeChanged(val value: String) : MapStoreMessage

    data class CreatePointTitleChanged(val value: String) : MapStoreMessage

    data class CreatePointCreated(val point: MapPoint) : MapStoreMessage

    object CreatePointSheetDismissed : MapStoreMessage

    data class DrawingStarted(val mode: MapStore.DrawingMode) : MapStoreMessage

    object ShapeSheetOpened : MapStoreMessage

    object DrawingDismissed : MapStoreMessage

    data class ShapeTitleChanged(val value: String) : MapStoreMessage

    object ShapeSheetDismissed : MapStoreMessage

    object FeatureInfoWindowDismissed : MapStoreMessage

    data class CurrentLocationMarkerUpdated(
        val mode: MyLocationMode,
        val marker: MapLocationMarker,
    ) : MapStoreMessage

    object RulerEnabled : MapStoreMessage

    object RulerDisabled : MapStoreMessage

    data class RulerMeasurementUpdated(
        val measurement: RulerMeasurement,
        val infoWindow: RulerInfoWindowState,
    ) : MapStoreMessage

    object RulerCleared : MapStoreMessage
}
