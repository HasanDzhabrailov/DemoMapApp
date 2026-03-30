package ru.tech.demomapapp.feature.map.impl.store

internal sealed interface MapStoreMessage {
    data class StateSynced(val state: MapStore.State) : MapStoreMessage

    object MapToolsMenuToggled : MapStoreMessage

    object MapToolsMenuDismissed : MapStoreMessage

    object CenterMarkerMenuOpened : MapStoreMessage

    object CenterMarkerMenuDismissed : MapStoreMessage

    object CreatePointSheetOpened : MapStoreMessage

    data class CreatePointLatitudeChanged(val value: String) : MapStoreMessage

    data class CreatePointLongitudeChanged(val value: String) : MapStoreMessage

    data class CreatePointTitleChanged(val value: String) : MapStoreMessage

    object CreatePointSheetDismissed : MapStoreMessage

    data class DrawingStarted(val mode: MapStore.DrawingMode) : MapStoreMessage

    object ShapeSheetOpened : MapStoreMessage

    object DrawingDismissed : MapStoreMessage

    data class ShapeTitleChanged(val value: String) : MapStoreMessage

    object ShapeSheetDismissed : MapStoreMessage

    object FeatureInfoWindowDismissed : MapStoreMessage
}
