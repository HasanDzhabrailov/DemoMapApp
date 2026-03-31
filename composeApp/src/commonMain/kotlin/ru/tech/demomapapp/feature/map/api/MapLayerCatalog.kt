package ru.tech.demomapapp.feature.map.api

object MapLayerCatalog {
    fun items(): List<MapCatalogItem> = listOf(
        MapCatalogItem(
            id = "demo-base",
            title = "DEM Map",
            kind = MapCatalogItemKind.BASE_MAP,
            source = MapLayerSourceRef.BaseStyle(MapStyle.DEMO),
        ),
        MapCatalogItem(
            id = "openstreetmap-base",
            title = "OpenStreetMap",
            kind = MapCatalogItemKind.BASE_MAP,
            source = MapLayerSourceRef.BaseStyle(MapStyle.OPEN_STREET_MAP),
        ),
        MapCatalogItem(
            id = "dem-overlay",
            title = "DEM Overlay Amazon",
            kind = MapCatalogItemKind.OVERLAY_LAYER,
            source = MapLayerSourceRef.RasterTileTemplate(
                templateId = "dem-overlay",
            ),
        ),
        MapCatalogItem(
            id = "google-overlay",
            title = "Google Map",
            kind = MapCatalogItemKind.OVERLAY_LAYER,
            source = MapLayerSourceRef.RasterTileTemplate(
                templateId = "google-overlay",
            ),
        ),
        MapCatalogItem(
            id = "yandex-overlay",
            title = "Yandex Map",
            kind = MapCatalogItemKind.OVERLAY_LAYER,
            source = MapLayerSourceRef.RasterTileTemplate(
                templateId = "yandex-overlay",
            ),
        ),
    )
}
