package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal class DefaultToolsComponent(
    componentContext: ComponentContext,
    private val toolsStoreFactory: ToolsStoreFactory,
    initialModel: MapScreenComponent.Model,
    private val output: ToolsComponent.Output,
) : ToolsComponent, ComponentContext by componentContext {

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        ToolsStoreHolder(
            toolsStoreFactory = toolsStoreFactory,
            initialModel = ToolsModel.fromModel(initialModel),
        )
    }
    private val labels = holder.labels(::handleLabel)
    private val states = holder.states { output.onStateChanged() }

    override val model: Value<ToolsModel> = holder.model

    override fun onMapToolsClick() = holder.accept(ToolsStore.Intent.MapToolsClicked)
    override fun onMapToolsDismiss() = holder.accept(ToolsStore.Intent.MapToolsDismissed)
    override fun onAvailableMapsClick() = holder.accept(ToolsStore.Intent.AvailableMapsClicked)
    override fun onAvailableMapsDismiss() = holder.accept(ToolsStore.Intent.AvailableMapsDismissed)
    override fun onAvailableMapSelect(mapId: String) = holder.accept(ToolsStore.Intent.AvailableMapSelected(mapId))
    override fun onAvailableMapConfirm() = holder.accept(ToolsStore.Intent.AvailableMapConfirmed)
    override fun onAvailableMapSelectionDismiss() = holder.accept(ToolsStore.Intent.AvailableMapSelectionDismissed)
    override fun onMapsOnScreenClick() = holder.accept(ToolsStore.Intent.MapsOnScreenClicked)
    override fun onMapsOnScreenDismiss() = holder.accept(ToolsStore.Intent.MapsOnScreenDismissed)
    override fun onLayerActionsClick(layerId: String) = holder.accept(ToolsStore.Intent.LayerActionsClicked(layerId))
    override fun onLayerActionsDismiss() = holder.accept(ToolsStore.Intent.LayerActionsDismissed)
    override fun onMoveLayerUpClick() = holder.accept(ToolsStore.Intent.MoveLayerUpClicked)
    override fun onMoveLayerDownClick() = holder.accept(ToolsStore.Intent.MoveLayerDownClicked)
    override fun onRemoveLayerClick() = holder.accept(ToolsStore.Intent.RemoveLayerClicked)
    override fun onLayerOpacityClick() = holder.accept(ToolsStore.Intent.LayerOpacityClicked)
    override fun onLayerOpacityChange(value: Float) = holder.accept(ToolsStore.Intent.LayerOpacityChanged(value))
    override fun onLayerOpacityDismiss() = holder.accept(ToolsStore.Intent.LayerOpacityDismissed)

    private fun handleLabel(label: ToolsStore.Label) {
        when (label) {
            is ToolsStore.Label.LayersChanged -> output.onLayersChanged(label.layers)
        }
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultToolsComponent.toolsStoreHolder"
    }
}
