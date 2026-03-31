package ru.tech.demomapapp.feature.map.render

internal sealed interface AndroidMapLayerDescriptor {
    data class RasterTileLayer(
        val key: String,
        val sourceId: String,
        val layerId: String,
        val urlTemplate: String,
        val attribution: String?,
        val tileSize: Int,
        val minZoom: Float,
        val maxZoom: Float,
        val opacity: Float,
    ) : AndroidMapLayerDescriptor
}

internal object MapLayerSourceConverter {
    fun convert(layer: RenderRasterTileLayer): AndroidMapLayerDescriptor.RasterTileLayer? {
        val normalizedKey = layer.key.replace("[^A-Za-z0-9_-]".toRegex(), "-")
        val tileDefinition = TILE_DEFINITIONS[layer.templateId] ?: return null
        return AndroidMapLayerDescriptor.RasterTileLayer(
            key = normalizedKey,
            sourceId = "map-renderer-raster-source-$normalizedKey",
            layerId = "map-renderer-raster-layer-$normalizedKey",
            urlTemplate = tileDefinition.urlTemplate,
            attribution = tileDefinition.attribution,
            tileSize = layer.tileSize,
            minZoom = layer.minZoom,
            maxZoom = layer.maxZoom,
            opacity = layer.opacity,
        )
    }

    private data class TileDefinition(
        val urlTemplate: String,
        val attribution: String,
    )

    private val TILE_DEFINITIONS = mapOf(
        "dem-overlay" to TileDefinition(
            urlTemplate = "https://s3.amazonaws.com/elevation-tiles-prod/normal/{z}/{x}/{y}.png",
            attribution = "DEM",
        ),
        "google-overlay" to TileDefinition(
            urlTemplate = "https://mt1.google.com/vt/lyrs=m&x={x}&y={y}&z={z}",
            attribution = "Google",
        ),
        "yandex-overlay" to TileDefinition(
            urlTemplate = "https://core-renderer-tiles.maps.yandex.net/tiles?l=map&x={x}&y={y}&z={z}&scale=1&lang=ru_RU",
            attribution = "Yandex",
        ),
    )
}
