package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.ToolsUiContract

/**
 * ToolsComponent extends ToolsUiContract to expose minimal UI interface.
 * Internal method (onMapToolsClick) remains in this interface only.
 */
interface ToolsComponent : ToolsUiContract {
    override val model: Value<ToolsModel>
    override val childSlot: Value<ChildSlot<*, ToolsUiContract.Child>>

    fun onMapToolsClick()
    override fun onMapToolsDismiss()
    override fun onAvailableMapsClick()
    override fun onAvailableMapsDismiss()
    override fun onAvailableMapSelect(mapId: String)
    override fun onAvailableMapConfirm()
    override fun onAvailableMapSelectionDismiss()
    override fun onMapsOnScreenClick()
    override fun onMapsOnScreenDismiss()
    override fun onLayerActionsClick(layerId: String)
    override fun onLayerActionsDismiss()
    override fun onMoveLayerUpClick()
    override fun onMoveLayerDownClick()
    override fun onRemoveLayerClick()
    override fun onLayerOpacityClick()
    override fun onLayerOpacityChange(value: Float)
    override fun onLayerOpacityDismiss()

    interface Output {
        fun onStateChanged()
        fun onLayersChanged(layers: List<MapLayerEntry>)
    }
}
