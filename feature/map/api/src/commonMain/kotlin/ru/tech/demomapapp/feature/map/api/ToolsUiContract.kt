package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

/**
 * UI contract for map tools.
 * Minimal interface exposing only what the UI needs.
 */
interface ToolsUiContract {
    val model: Value<ToolsModel>
    val childSlot: Value<ChildSlot<*, Child>>

    fun onMapToolsDismiss()
    fun onAvailableMapsClick()
    fun onAvailableMapsDismiss()
    fun onAvailableMapSelect(mapId: String)
    fun onAvailableMapConfirm()
    fun onAvailableMapSelectionDismiss()
    fun onMapsOnScreenClick()
    fun onMapsOnScreenDismiss()
    fun onLayerActionsClick(layerId: String)
    fun onLayerActionsDismiss()
    fun onMoveLayerUpClick()
    fun onMoveLayerDownClick()
    fun onRemoveLayerClick()
    fun onLayerOpacityClick()
    fun onLayerOpacityChange(value: Float)
    fun onLayerOpacityDismiss()

    interface Child {
        data object Menu : Child
        data object AvailableMaps : Child
        data object ConfirmAddMap : Child
        data object MapsOnScreen : Child
        data object LayerActions : Child
        data object LayerOpacity : Child
    }
}

/**
 * Model for tools UI state.
 * Defined in API to avoid internal imports.
 */
data class ToolsModel(
    val availableMapCatalog: List<MapCatalogItem> = MapLayerCatalog.items(),
    val selectedAvailableMap: MapCatalogItem? = null,
    val selectedOverlayLayer: MapLayerEntry? = null,
    val editingOverlayOpacityLayer: MapLayerEntry? = null,
    val layers: List<MapLayerEntry> = emptyList(),
    val selectedStyle: MapStyle = MapStyle.DEMO,
)
