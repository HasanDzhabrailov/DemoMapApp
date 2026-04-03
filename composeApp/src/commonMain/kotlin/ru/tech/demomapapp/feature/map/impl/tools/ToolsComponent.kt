package ru.tech.demomapapp.feature.map.impl.tools

import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapLayerEntry

interface ToolsComponent {
    val model: Value<ToolsModel>

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

    interface Output {
        fun onStateChanged()
        fun onLayersChanged(layers: List<MapLayerEntry>)
    }
}
