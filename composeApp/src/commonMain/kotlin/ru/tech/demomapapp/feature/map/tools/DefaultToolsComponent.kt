package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable
import ru.tech.demomapapp.feature.map.api.MapCatalogItemKind
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.tools.fromModel

internal class DefaultToolsComponent(
    componentContext: ComponentContext,
    private val toolsStoreFactory: ToolsStoreFactory,
    initialModel: MapScreenComponent.Model,
    private val output: ToolsComponent.Output,
) : ToolsComponent, ComponentContext by componentContext {
    private val navigation = SlotNavigation<Config>()
    
    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        ToolsStoreHolder(
            toolsStoreFactory = toolsStoreFactory,
            initialModel = fromModel(initialModel),
        )
    }
    private val labels = holder.labels(::handleLabel)
    private val states = holder.states { output.onStateChanged() }

    override val model: Value<ToolsModel> = holder.model
    override val childSlot: Value<ChildSlot<*, ToolsComponent.Child>> = childSlot(
        source = navigation,
        serializer = Config.serializer(),
        handleBackButton = false,
        childFactory = ::createChild,
    )

    override fun onMapToolsClick() {
        holder.accept(ToolsStore.Intent.MapToolsClicked)
        if (activeChild() is ToolsComponent.Child.Menu) {
            dismissChild()
        } else {
            activateChild(Config.Menu)
        }
    }

    override fun onMapToolsDismiss() {
        holder.accept(ToolsStore.Intent.MapToolsDismissed)
        dismissChild()
    }

    override fun onAvailableMapsClick() {
        holder.accept(ToolsStore.Intent.AvailableMapsClicked)
        activateChild(Config.AvailableMaps)
    }

    override fun onAvailableMapsDismiss() {
        holder.accept(ToolsStore.Intent.AvailableMapsDismissed)
        dismissChild()
    }

    override fun onAvailableMapSelect(mapId: String) {
        holder.accept(ToolsStore.Intent.AvailableMapSelected(mapId))
        if (model.value.selectedAvailableMap != null) {
            activateChild(Config.ConfirmAddMap)
        }
    }

    override fun onAvailableMapConfirm() {
        val selectedMap = model.value.selectedAvailableMap
        holder.accept(ToolsStore.Intent.AvailableMapConfirmed)
        when (selectedMap?.kind) {
            MapCatalogItemKind.OVERLAY_LAYER -> activateChild(Config.MapsOnScreen)
            MapCatalogItemKind.BASE_MAP, null -> dismissChild()
        }
    }

    override fun onAvailableMapSelectionDismiss() {
        holder.accept(ToolsStore.Intent.AvailableMapSelectionDismissed)
        activateChild(Config.AvailableMaps)
    }

    override fun onMapsOnScreenClick() {
        holder.accept(ToolsStore.Intent.MapsOnScreenClicked)
        activateChild(Config.MapsOnScreen)
    }

    override fun onMapsOnScreenDismiss() {
        holder.accept(ToolsStore.Intent.MapsOnScreenDismissed)
        dismissChild()
    }

    override fun onLayerActionsClick(layerId: String) {
        holder.accept(ToolsStore.Intent.LayerActionsClicked(layerId))
        if (model.value.selectedOverlayLayer != null) {
            activateChild(Config.LayerActions)
        }
    }

    override fun onLayerActionsDismiss() {
        holder.accept(ToolsStore.Intent.LayerActionsDismissed)
        activateChild(Config.MapsOnScreen)
    }

    override fun onMoveLayerUpClick() = holder.accept(ToolsStore.Intent.MoveLayerUpClicked)
    override fun onMoveLayerDownClick() = holder.accept(ToolsStore.Intent.MoveLayerDownClicked)

    override fun onRemoveLayerClick() {
        holder.accept(ToolsStore.Intent.RemoveLayerClicked)
        activateChild(Config.MapsOnScreen)
    }

    override fun onLayerOpacityClick() {
        holder.accept(ToolsStore.Intent.LayerOpacityClicked)
        if (model.value.editingOverlayOpacityLayer != null) {
            activateChild(Config.LayerOpacity)
        }
    }

    override fun onLayerOpacityChange(value: Float) = holder.accept(ToolsStore.Intent.LayerOpacityChanged(value))

    override fun onLayerOpacityDismiss() {
        holder.accept(ToolsStore.Intent.LayerOpacityDismissed)
        activateChild(Config.MapsOnScreen)
    }

    private fun handleLabel(label: ToolsStore.Label) {
        when (label) {
            is ToolsStore.Label.LayersChanged -> output.onLayersChanged(label.layers)
        }
    }

    private fun createChild(config: Config, componentContext: ComponentContext): ToolsComponent.Child = when (config) {
        Config.Menu -> ToolsComponent.Child.Menu
        Config.AvailableMaps -> ToolsComponent.Child.AvailableMaps
        Config.ConfirmAddMap -> ToolsComponent.Child.ConfirmAddMap
        Config.MapsOnScreen -> ToolsComponent.Child.MapsOnScreen
        Config.LayerActions -> ToolsComponent.Child.LayerActions
        Config.LayerOpacity -> ToolsComponent.Child.LayerOpacity
    }

    private fun activeChild(): ToolsComponent.Child? = childSlot.value.child?.instance

    private fun activateChild(config: Config) {
        navigation.activate(config)
        output.onStateChanged()
    }

    private fun dismissChild() {
        navigation.dismiss()
        output.onStateChanged()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Menu : Config

        @Serializable
        data object AvailableMaps : Config

        @Serializable
        data object ConfirmAddMap : Config

        @Serializable
        data object MapsOnScreen : Config

        @Serializable
        data object LayerActions : Config

        @Serializable
        data object LayerOpacity : Config
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultToolsComponent.toolsStoreHolder"
    }
}
