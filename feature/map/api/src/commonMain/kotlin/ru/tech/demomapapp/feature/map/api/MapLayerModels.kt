package ru.tech.demomapapp.feature.map.api

data class MapCatalogItem(
    val id: String,
    val title: String,
    val kind: MapCatalogItemKind,
    val source: MapLayerSourceRef,
)

enum class MapCatalogItemKind {
    BASE_MAP,
    OVERLAY_LAYER,
}

data class MapLayerEntry(
    val id: String,
    val title: String,
    val source: MapLayerSourceRef.RasterTileTemplate,
    val opacity: Float = 1f,
)

sealed interface MapLayerSourceRef {
    data class BaseStyle(
        val mapStyle: MapStyle,
    ) : MapLayerSourceRef

    data class RasterTileTemplate(
        val templateId: String,
        val tileSize: Int = 256,
        val minZoom: Float = 0f,
        val maxZoom: Float = 20f,
    ) : MapLayerSourceRef
}
