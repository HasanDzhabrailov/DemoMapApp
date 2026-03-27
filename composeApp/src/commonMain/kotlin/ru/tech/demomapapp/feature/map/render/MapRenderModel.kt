package ru.tech.demomapapp.feature.map.render

data class MapRenderModel(
    val style: RenderMapStyle = RenderMapStyle.DEFAULT,
    val points: List<RenderMapPoint> = emptyList(),
    val lines: List<RenderMapLine> = emptyList(),
    val polygons: List<RenderMapPolygon> = emptyList(),
    val drawingPreview: RenderDrawingPreview? = null,
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
}
