package ru.tech.demomapapp.feature.map.render

data class MapRenderModel(
    val style: RenderMapStyle = RenderMapStyle.DEFAULT,
    val tileLayers: List<RenderRasterTileLayer> = emptyList(),
    val points: List<RenderMapPoint> = emptyList(),
    val lines: List<RenderMapLine> = emptyList(),
    val polygons: List<RenderMapPolygon> = emptyList(),
    val currentLocationMarker: RenderCurrentLocationMarker? = null,
    val rulerMeasurement: RenderRulerMeasurement? = null,
    val drawingPreview: RenderDrawingPreview? = null,
)

data class RenderCurrentLocationMarker(
    val latitude: Double,
    val longitude: Double,
    val isPlaceholder: Boolean,
)

data class RenderRulerMeasurement(
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val arrowSegments: List<RenderRulerArrowSegment> = emptyList(),
)

data class RenderRulerArrowSegment(
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
)

data class RenderMapPoint(
    val key: String,
    val latitude: Double,
    val longitude: Double,
    val label: String,
)

data class RenderMapVertex(
    val latitude: Double,
    val longitude: Double,
)

data class RenderMapLine(
    val key: String,
    val vertices: List<RenderMapVertex>,
    val label: String,
    val labelLatitude: Double,
    val labelLongitude: Double,
)

data class RenderMapPolygon(
    val key: String,
    val vertices: List<RenderMapVertex>,
    val label: String,
    val labelLatitude: Double,
    val labelLongitude: Double,
)

data class RenderDrawingPreview(
    val fixedLineVertices: List<RenderMapVertex> = emptyList(),
    val previewLineVertices: List<RenderMapVertex> = emptyList(),
    val fixedPolygonVertices: List<RenderMapVertex> = emptyList(),
)

data class RenderRasterTileLayer(
    val key: String,
    val title: String,
    val templateId: String,
    val tileSize: Int,
    val minZoom: Float,
    val maxZoom: Float,
    val opacity: Float,
)

data class RenderFeatureClick(
    val featureKey: String,
    val featureType: RenderFeatureType,
    val anchor: RenderFeatureAnchor,
)

data class RenderFeatureAnchor(
    val screenX: Int,
    val screenY: Int,
)

enum class RenderFeatureType {
    POINT,
    LINE,
    POLYGON,
}

enum class RenderMapStyle {
    DEFAULT,
    OPEN_STREET_MAP,
}
