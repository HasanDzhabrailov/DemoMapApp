package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.MapCatalogItemKind
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLayerSourceRef

internal fun MapStore.State.reduceLayerManagementMessage(msg: MapStoreMessage): MapStore.State? = when (msg) {
    is MapStoreMessage.AvailableMapsOpened -> copy(
        isMapToolsMenuVisible = false,
        isAvailableMapsSheetVisible = true,
        isMapsOnScreenSheetVisible = false,
        selectedAvailableMap = null,
        selectedOverlayLayer = null,
        editingOverlayOpacityLayer = null,
        selectedFeatureInfoWindow = null,
    )

    is MapStoreMessage.AvailableMapsDismissed -> copy(
        isAvailableMapsSheetVisible = false,
        selectedAvailableMap = null,
    )

    is MapStoreMessage.AvailableMapSelected -> copy(
        selectedAvailableMap = availableMapCatalog.firstOrNull { it.id == msg.mapId },
    )

    is MapStoreMessage.AvailableMapSelectionDismissed -> copy(
        selectedAvailableMap = null,
    )

    is MapStoreMessage.AvailableMapConfirmed -> addSelectedAvailableMap()

    is MapStoreMessage.MapsOnScreenOpened -> copy(
        isMapToolsMenuVisible = false,
        isAvailableMapsSheetVisible = false,
        isMapsOnScreenSheetVisible = true,
        selectedAvailableMap = null,
        selectedFeatureInfoWindow = null,
    )

    is MapStoreMessage.MapsOnScreenDismissed -> copy(
        isMapsOnScreenSheetVisible = false,
        selectedOverlayLayer = null,
        editingOverlayOpacityLayer = null,
    )

    is MapStoreMessage.OverlayLayerActionsOpened -> copy(
        selectedOverlayLayer = mapState.overlayLayers.firstOrNull { it.id == msg.layerId },
        editingOverlayOpacityLayer = null,
    )

    is MapStoreMessage.OverlayLayerActionsDismissed -> copy(
        selectedOverlayLayer = null,
    )

    is MapStoreMessage.OverlayLayerMovedUp -> moveSelectedOverlayLayer(step = 1)

    is MapStoreMessage.OverlayLayerMovedDown -> moveSelectedOverlayLayer(step = -1)

    is MapStoreMessage.OverlayLayerRemoved -> removeSelectedOverlayLayer()

    is MapStoreMessage.OverlayLayerOpacityEditorOpened -> copy(
        editingOverlayOpacityLayer = selectedOverlayLayer,
        selectedOverlayLayer = null,
    )

    is MapStoreMessage.OverlayLayerOpacityChanged -> updateOverlayOpacity(msg.value)

    is MapStoreMessage.OverlayLayerOpacityEditorDismissed -> copy(
        editingOverlayOpacityLayer = null,
    )

    else -> null
}

private fun MapStore.State.addSelectedAvailableMap(): MapStore.State {
    val selectedMap = selectedAvailableMap ?: return this
    return when (selectedMap.kind) {
        MapCatalogItemKind.BASE_MAP -> {
            val source = selectedMap.source as? MapLayerSourceRef.BaseStyle ?: return this
            copy(
                mapState = mapState.copy(style = source.mapStyle),
                isAvailableMapsSheetVisible = false,
                selectedAvailableMap = null,
            )
        }

        MapCatalogItemKind.OVERLAY_LAYER -> {
            val source = selectedMap.source as? MapLayerSourceRef.RasterTileTemplate ?: return this
            val updatedLayers = mapState.overlayLayers.filterNot { it.id == selectedMap.id } +
                MapLayerEntry(
                    id = selectedMap.id,
                    title = selectedMap.title,
                    source = source,
                )
            copy(
                mapState = mapState.copy(overlayLayers = updatedLayers),
                isAvailableMapsSheetVisible = false,
                selectedAvailableMap = null,
                isMapsOnScreenSheetVisible = true,
            )
        }
    }
}

private fun MapStore.State.moveSelectedOverlayLayer(step: Int): MapStore.State {
    val selected = selectedOverlayLayer ?: return this
    val layers = mapState.overlayLayers.toMutableList()
    val currentIndex = layers.indexOfFirst { it.id == selected.id }
    if (currentIndex == -1) {
        return copy(selectedOverlayLayer = null)
    }
    val targetIndex = currentIndex + step
    if (targetIndex !in layers.indices) {
        return this
    }
    val layer = layers.removeAt(currentIndex)
    layers.add(targetIndex, layer)
    return copy(
        mapState = mapState.copy(overlayLayers = layers),
        selectedOverlayLayer = layers[targetIndex],
    )
}

private fun MapStore.State.removeSelectedOverlayLayer(): MapStore.State {
    val selected = selectedOverlayLayer ?: editingOverlayOpacityLayer ?: return this
    val updatedLayers = mapState.overlayLayers.filterNot { it.id == selected.id }
    return copy(
        mapState = mapState.copy(overlayLayers = updatedLayers),
        selectedOverlayLayer = null,
        editingOverlayOpacityLayer = null,
    )
}

private fun MapStore.State.updateOverlayOpacity(value: Float): MapStore.State {
    val selected = editingOverlayOpacityLayer ?: return this
    val normalizedValue = value.coerceIn(0f, 1f)
    val updatedLayers = mapState.overlayLayers.map { layer ->
        if (layer.id == selected.id) {
            layer.copy(opacity = normalizedValue)
        } else {
            layer
        }
    }
    val updatedSelectedLayer = updatedLayers.firstOrNull { it.id == selected.id }
    return copy(
        mapState = mapState.copy(overlayLayers = updatedLayers),
        editingOverlayOpacityLayer = updatedSelectedLayer,
    )
}
