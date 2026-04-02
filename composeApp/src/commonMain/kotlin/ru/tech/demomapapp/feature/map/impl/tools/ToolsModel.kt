package ru.tech.demomapapp.feature.map.impl.tools

import ru.tech.demomapapp.feature.map.api.MapCatalogItem
import ru.tech.demomapapp.feature.map.api.MapLayerCatalog
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapStyle

data class ToolsModel(
    val isMenuVisible: Boolean = false,
    val isAvailableMapsSheetVisible: Boolean = false,
    val availableMapCatalog: List<MapCatalogItem> = MapLayerCatalog.items(),
    val selectedAvailableMap: MapCatalogItem? = null,
    val isMapsOnScreenSheetVisible: Boolean = false,
    val selectedOverlayLayer: MapLayerEntry? = null,
    val editingOverlayOpacityLayer: MapLayerEntry? = null,
    val layers: List<MapLayerEntry> = emptyList(),
    val selectedStyle: MapStyle = MapStyle.DEMO,
) {
    companion object {
        fun fromModel(model: MapScreenComponent.Model): ToolsModel = ToolsModel(
            isMenuVisible = model.isMapToolsMenuVisible,
            isAvailableMapsSheetVisible = model.isAvailableMapsSheetVisible,
            availableMapCatalog = model.availableMapCatalog,
            selectedAvailableMap = model.selectedAvailableMap,
            isMapsOnScreenSheetVisible = model.isMapsOnScreenSheetVisible,
            selectedOverlayLayer = model.selectedOverlayLayer,
            editingOverlayOpacityLayer = model.editingOverlayOpacityLayer,
            layers = model.mapState.overlayLayers,
            selectedStyle = model.mapState.style,
        )
    }
}
