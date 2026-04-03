package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapLayerEntry

interface ToolsComponent {
    val model: Value<ToolsModel>
    val childSlot: Value<ChildSlot<*, Child>>

    fun onMapToolsClick()
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

    sealed interface Child {
        data object Menu : Child
        data object AvailableMaps : Child
        data object ConfirmAddMap : Child
        data object MapsOnScreen : Child
        data object LayerActions : Child
        data object LayerOpacity : Child
    }

    interface Output {
        fun onStateChanged()
        fun onLayersChanged(layers: List<MapLayerEntry>)
    }
}
