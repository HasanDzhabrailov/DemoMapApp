package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.PropertyFactory.rasterOpacity
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

internal fun Style.applyRasterTileLayers(layers: List<RenderRasterTileLayer>) {
    clearManagedRasterLayers()
    layers.mapNotNull(MapLayerSourceConverter::convert).forEach { descriptor ->
        val tileSet = TileSet("2.1.0", descriptor.urlTemplate)
        descriptor.attribution?.let { tileSet.attribution = it }
        addSource(RasterSource(descriptor.sourceId, tileSet, descriptor.tileSize))
        addManagedRasterLayer(
            RasterLayer(descriptor.layerId, descriptor.sourceId).withProperties(
                rasterOpacity(descriptor.opacity),
            ),
        )
    }
}

private fun Style.addManagedRasterLayer(layer: RasterLayer) {
    val anchorLayerId = firstManagedOverlayAnchorLayerId()
    if (anchorLayerId == null) {
        addLayer(layer)
    } else {
        addLayerBelow(layer, anchorLayerId)
    }
}

private fun Style.firstManagedOverlayAnchorLayerId(): String? = MAP_MANAGED_OVERLAY_LAYER_IDS.firstOrNull { layerId ->
    getLayer(layerId) != null
}

private fun Style.clearManagedRasterLayers() {
    val layerIds = layers.mapNotNull { layer ->
        layer.id.takeIf { it.startsWith("map-renderer-raster-layer-") }
    }
    layerIds.forEach(::removeLayer)

    val sourceIds = sources.mapNotNull { source ->
        source.id.takeIf { it.startsWith("map-renderer-raster-source-") }
    }
    sourceIds.forEach(::removeSource)
}
