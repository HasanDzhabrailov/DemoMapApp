package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapCatalogItem
import ru.tech.demomapapp.feature.map.api.MapLayerCatalog
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapStyle

internal interface ToolsStore : Store<ToolsStore.Intent, ToolsStore.State, ToolsStore.Label> {
    sealed interface Intent {
        object MapToolsClicked : Intent
        object MapToolsDismissed : Intent
        object AvailableMapsClicked : Intent
        object AvailableMapsDismissed : Intent
        data class AvailableMapSelected(val mapId: String) : Intent
        object AvailableMapConfirmed : Intent
        object AvailableMapSelectionDismissed : Intent
        object MapsOnScreenClicked : Intent
        object MapsOnScreenDismissed : Intent
        data class LayerActionsClicked(val layerId: String) : Intent
        object LayerActionsDismissed : Intent
        object MoveLayerUpClicked : Intent
        object MoveLayerDownClicked : Intent
        object RemoveLayerClicked : Intent
        object LayerOpacityClicked : Intent
        data class LayerOpacityChanged(val value: Float) : Intent
        object LayerOpacityDismissed : Intent
    }

    data class State(
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
        fun toModel(): ToolsModel = ToolsModel(
            isMenuVisible = isMenuVisible,
            isAvailableMapsSheetVisible = isAvailableMapsSheetVisible,
            availableMapCatalog = availableMapCatalog,
            selectedAvailableMap = selectedAvailableMap,
            isMapsOnScreenSheetVisible = isMapsOnScreenSheetVisible,
            selectedOverlayLayer = selectedOverlayLayer,
            editingOverlayOpacityLayer = editingOverlayOpacityLayer,
            layers = layers,
            selectedStyle = selectedStyle,
        )

        companion object {
            fun fromModel(model: ToolsModel): State = State(
                isMenuVisible = model.isMenuVisible,
                isAvailableMapsSheetVisible = model.isAvailableMapsSheetVisible,
                availableMapCatalog = model.availableMapCatalog,
                selectedAvailableMap = model.selectedAvailableMap,
                isMapsOnScreenSheetVisible = model.isMapsOnScreenSheetVisible,
                selectedOverlayLayer = model.selectedOverlayLayer,
                editingOverlayOpacityLayer = model.editingOverlayOpacityLayer,
                layers = model.layers,
                selectedStyle = model.selectedStyle,
            )
        }
    }

    sealed interface Message {
        object MapToolsMenuToggled : Message
        object MapToolsMenuDismissed : Message
        object AvailableMapsOpened : Message
        object AvailableMapsDismissed : Message
        data class AvailableMapSelected(val mapId: String) : Message
        object AvailableMapConfirmed : Message
        object AvailableMapSelectionDismissed : Message
        object MapsOnScreenOpened : Message
        object MapsOnScreenDismissed : Message
        data class LayerActionsOpened(val layerId: String) : Message
        object LayerActionsDismissed : Message
        object LayerMovedUp : Message
        object LayerMovedDown : Message
        object LayerRemoved : Message
        object LayerOpacityEditorOpened : Message
        data class LayerOpacityChanged(val value: Float) : Message
        object LayerOpacityEditorDismissed : Message
    }

    sealed interface Label {
        data class LayersChanged(val layers: List<MapLayerEntry>) : Label
    }
}
