package ru.tech.demomapapp.feature.map.tools

import ru.tech.demomapapp.feature.map.api.MapScreenComponent

/**
 * ToolsModel is now defined in the API package.
 * This typealias preserves backward compatibility.
 */
typealias ToolsModel = ru.tech.demomapapp.feature.map.api.ToolsModel

/**
 * Factory function to create ToolsModel from MapScreenComponent.Model.
 */
fun fromModel(model: MapScreenComponent.Model): ToolsModel = ToolsModel(
    availableMapCatalog = model.availableMapCatalog,
    selectedAvailableMap = model.selectedAvailableMap,
    selectedOverlayLayer = model.selectedOverlayLayer,
    editingOverlayOpacityLayer = model.editingOverlayOpacityLayer,
    layers = model.mapState.overlayLayers,
    selectedStyle = model.mapState.style,
)