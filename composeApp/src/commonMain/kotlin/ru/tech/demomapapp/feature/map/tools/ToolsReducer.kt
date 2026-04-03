package ru.tech.demomapapp.feature.map.tools

import ru.tech.demomapapp.feature.map.api.MapCatalogItemKind
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLayerSourceRef

internal object ToolsReducer {

    fun reduce(state: ToolsStore.State, msg: ToolsStore.Message): ToolsStore.State = with(state) {
        when (msg) {
            is ToolsStore.Message.MapToolsMenuToggled -> {
                val isMenuVisible = !isMenuVisible
                copy(
                    isMenuVisible = isMenuVisible,
                    isAvailableMapsSheetVisible = false,
                    isMapsOnScreenSheetVisible = false,
                    selectedAvailableMap = null,
                    selectedOverlayLayer = null,
                    editingOverlayOpacityLayer = null,
                )
            }

            is ToolsStore.Message.MapToolsMenuDismissed -> copy(isMenuVisible = false)

            is ToolsStore.Message.AvailableMapsOpened -> copy(
                isMenuVisible = false,
                isAvailableMapsSheetVisible = true,
                isMapsOnScreenSheetVisible = false,
                selectedAvailableMap = null,
                selectedOverlayLayer = null,
                editingOverlayOpacityLayer = null,
            )

            is ToolsStore.Message.AvailableMapsDismissed -> copy(
                isAvailableMapsSheetVisible = false,
                selectedAvailableMap = null,
            )

            is ToolsStore.Message.AvailableMapSelected -> copy(
                selectedAvailableMap = availableMapCatalog.firstOrNull { it.id == msg.mapId },
            )

            is ToolsStore.Message.AvailableMapConfirmed -> addSelectedAvailableMap()

            is ToolsStore.Message.AvailableMapSelectionDismissed -> copy(selectedAvailableMap = null)

            is ToolsStore.Message.MapsOnScreenOpened -> copy(
                isMenuVisible = false,
                isAvailableMapsSheetVisible = false,
                isMapsOnScreenSheetVisible = true,
                selectedAvailableMap = null,
            )

            is ToolsStore.Message.MapsOnScreenDismissed -> copy(
                isMapsOnScreenSheetVisible = false,
                selectedOverlayLayer = null,
                editingOverlayOpacityLayer = null,
            )

            is ToolsStore.Message.LayerActionsOpened -> copy(
                selectedOverlayLayer = layers.firstOrNull { it.id == msg.layerId },
                editingOverlayOpacityLayer = null,
            )

            is ToolsStore.Message.LayerActionsDismissed -> copy(selectedOverlayLayer = null)

            is ToolsStore.Message.LayerMovedUp -> moveSelectedOverlayLayer(step = 1)

            is ToolsStore.Message.LayerMovedDown -> moveSelectedOverlayLayer(step = -1)

            is ToolsStore.Message.LayerRemoved -> removeSelectedOverlayLayer()

            is ToolsStore.Message.LayerOpacityEditorOpened -> copy(
                editingOverlayOpacityLayer = selectedOverlayLayer,
                selectedOverlayLayer = null,
            )

            is ToolsStore.Message.LayerOpacityChanged -> updateOverlayOpacity(msg.value)

            is ToolsStore.Message.LayerOpacityEditorDismissed -> copy(editingOverlayOpacityLayer = null)
        }
    }

    private fun ToolsStore.State.addSelectedAvailableMap(): ToolsStore.State {
        val selectedMap = selectedAvailableMap ?: return this
        return when (selectedMap.kind) {
            MapCatalogItemKind.BASE_MAP -> {
                val source = selectedMap.source as? MapLayerSourceRef.BaseStyle ?: return this
                copy(
                    selectedStyle = source.mapStyle,
                    isAvailableMapsSheetVisible = false,
                    selectedAvailableMap = null,
                )
            }

            MapCatalogItemKind.OVERLAY_LAYER -> {
                val source = selectedMap.source as? MapLayerSourceRef.RasterTileTemplate ?: return this
                val updatedLayers = layers.filterNot { it.id == selectedMap.id } +
                    MapLayerEntry(
                        id = selectedMap.id,
                        title = selectedMap.title,
                        source = source,
                    )
                copy(
                    layers = updatedLayers,
                    isAvailableMapsSheetVisible = false,
                    selectedAvailableMap = null,
                    isMapsOnScreenSheetVisible = true,
                )
            }
        }
    }

    private fun ToolsStore.State.moveSelectedOverlayLayer(step: Int): ToolsStore.State {
        val selected = selectedOverlayLayer ?: return this
        val updatedLayers = layers.toMutableList()
        val currentIndex = updatedLayers.indexOfFirst { it.id == selected.id }
        if (currentIndex == -1) {
            return copy(selectedOverlayLayer = null)
        }
        val targetIndex = currentIndex + step
        if (targetIndex !in updatedLayers.indices) {
            return this
        }
        val layer = updatedLayers.removeAt(currentIndex)
        updatedLayers.add(targetIndex, layer)
        return copy(
            layers = updatedLayers,
            selectedOverlayLayer = updatedLayers[targetIndex],
        )
    }

    private fun ToolsStore.State.removeSelectedOverlayLayer(): ToolsStore.State {
        val selected = selectedOverlayLayer ?: editingOverlayOpacityLayer ?: return this
        val updatedLayers = layers.filterNot { it.id == selected.id }
        return copy(
            layers = updatedLayers,
            selectedOverlayLayer = null,
            editingOverlayOpacityLayer = null,
        )
    }

    private fun ToolsStore.State.updateOverlayOpacity(value: Float): ToolsStore.State {
        val selected = editingOverlayOpacityLayer ?: return this
        val normalizedValue = value.coerceIn(0f, 1f)
        val updatedLayers = layers.map { layer ->
            if (layer.id == selected.id) {
                layer.copy(opacity = normalizedValue)
            } else {
                layer
            }
        }
        return copy(
            layers = updatedLayers,
            editingOverlayOpacityLayer = updatedLayers.firstOrNull { it.id == selected.id },
        )
    }
}
