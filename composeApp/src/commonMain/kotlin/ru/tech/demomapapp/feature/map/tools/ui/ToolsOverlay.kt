package ru.tech.demomapapp.feature.map.tools.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.tools.ToolsComponent

@Composable
internal fun ToolsOverlay(
    component: ToolsComponent,
    isGpsEnabled: Boolean,
    isRulerEnabled: Boolean,
    onDismiss: () -> Unit,
    onGpsToggle: () -> Unit,
    onRulerToggle: () -> Unit,
) {
    val model by component.model.subscribeAsState()
    val childSlot by component.childSlot.subscribeAsState()
    val child = childSlot.child?.instance

    if (child is ToolsComponent.Child.Menu) {
        MapToolsMenuOverlay(
            isGpsEnabled = isGpsEnabled,
            isRulerEnabled = isRulerEnabled,
            onDismiss = onDismiss,
            onAvailableMapsClick = component::onAvailableMapsClick,
            onMapsOnScreenClick = component::onMapsOnScreenClick,
            onGpsToggle = onGpsToggle,
            onRulerToggle = onRulerToggle,
        )
    }

    if (child is ToolsComponent.Child.AvailableMaps || child is ToolsComponent.Child.ConfirmAddMap) {
        AvailableMapsBottomSheet(
            items = model.availableMapCatalog,
            onSelect = component::onAvailableMapSelect,
            onDismiss = component::onAvailableMapsDismiss,
        )
    }

    if (child is ToolsComponent.Child.ConfirmAddMap) {
        model.selectedAvailableMap?.let { selectedMap ->
            ConfirmAddMapDialog(
                mapTitle = selectedMap.title,
                onConfirm = component::onAvailableMapConfirm,
                onDismiss = component::onAvailableMapSelectionDismiss,
            )
        }
    }

    if (
        child is ToolsComponent.Child.MapsOnScreen ||
        child is ToolsComponent.Child.LayerActions ||
        child is ToolsComponent.Child.LayerOpacity
    ) {
        MapsOnScreenBottomSheet(
            baseMapTitle = model.selectedStyle.displayName(),
            layers = model.layers,
            onLayerActionsClick = component::onLayerActionsClick,
            onDismiss = component::onMapsOnScreenDismiss,
        )
    }

    if (child is ToolsComponent.Child.LayerActions) {
        model.selectedOverlayLayer?.let { selectedLayer ->
            val visibleLayers = model.layers.asReversed()
            val selectedLayerIndex = visibleLayers.indexOfFirst { it.id == selectedLayer.id }
            LayerActionsDialog(
                layer = selectedLayer,
                canMoveUp = selectedLayerIndex > 0,
                canMoveDown = selectedLayerIndex in 0 until visibleLayers.lastIndex,
                onMoveUp = component::onMoveLayerUpClick,
                onMoveDown = component::onMoveLayerDownClick,
                onChangeOpacity = component::onLayerOpacityClick,
                onRemove = component::onRemoveLayerClick,
                onDismiss = component::onLayerActionsDismiss,
            )
        }
    }

    if (child is ToolsComponent.Child.LayerOpacity) {
        model.editingOverlayOpacityLayer?.let { selectedLayer ->
            LayerOpacityBottomSheet(
                layerTitle = selectedLayer.title,
                opacity = selectedLayer.opacity,
                onOpacityChange = component::onLayerOpacityChange,
                onDismiss = component::onLayerOpacityDismiss,
            )
        }
    }
}

private fun MapStyle.displayName(): String = when (this) {
    MapStyle.DEMO -> "DEM Map"
    MapStyle.OPEN_STREET_MAP -> "OpenStreetMap"
}
