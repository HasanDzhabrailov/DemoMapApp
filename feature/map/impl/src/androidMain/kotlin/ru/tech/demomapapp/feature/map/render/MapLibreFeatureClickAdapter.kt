package ru.tech.demomapapp.feature.map.render

import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.roundToInt
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

private const val MAP_FEATURE_TAP_TOLERANCE_PX = 24f

internal class MapLibreFeatureClickAdapter(
    private val onFeatureClick: (RenderFeatureClick) -> Unit,
) {
    private var map: MapLibreMap? = null
    private var listener: MapLibreMap.OnMapClickListener? = null

    fun attach(map: MapLibreMap) {
        if (this.map === map && listener != null) {
            return
        }

        detach()

        val clickListener = MapLibreMap.OnMapClickListener { latLng ->
            map.toRenderFeatureClick(latLng)?.also(onFeatureClick) != null
        }

        this.map = map
        listener = clickListener
        map.addOnMapClickListener(clickListener)
    }

    fun detach() {
        val currentMap = map
        val currentListener = listener
        if (currentMap != null && currentListener != null) {
            currentMap.removeOnMapClickListener(currentListener)
        }

        map = null
        listener = null
    }
}

private fun MapLibreMap.toRenderFeatureClick(latLng: LatLng): RenderFeatureClick? {
    val tapPoint = projection.toScreenLocation(latLng)
    val feature = queryRenderedFeatures(
        RectF(
            tapPoint.x - MAP_FEATURE_TAP_TOLERANCE_PX,
            tapPoint.y - MAP_FEATURE_TAP_TOLERANCE_PX,
            tapPoint.x + MAP_FEATURE_TAP_TOLERANCE_PX,
            tapPoint.y + MAP_FEATURE_TAP_TOLERANCE_PX,
        ),
        MAP_POINTS_LAYER_ID,
        MAP_LINES_LAYER_ID,
        MAP_POLYGONS_FILL_LAYER_ID,
        MAP_POLYGONS_OUTLINE_LAYER_ID,
    ).firstOrNull() ?: return null

    val featureKey = feature.getStringProperty(MAP_FEATURE_KEY_PROPERTY).takeIf(String::isNotBlank) ?: return null
    val featureType = feature.getStringProperty(MAP_FEATURE_TYPE_PROPERTY).toRenderFeatureType() ?: return null

    return RenderFeatureClick(
        featureKey = featureKey,
        featureType = featureType,
        anchor = tapPoint.toRenderFeatureAnchor(),
    )
}

private fun String?.toRenderFeatureType(): RenderFeatureType? = when (this) {
    RenderFeatureType.POINT.name -> RenderFeatureType.POINT
    RenderFeatureType.LINE.name -> RenderFeatureType.LINE
    RenderFeatureType.POLYGON.name -> RenderFeatureType.POLYGON
    else -> null
}

private fun PointF.toRenderFeatureAnchor(): RenderFeatureAnchor = RenderFeatureAnchor(
    screenX = x.roundToInt(),
    screenY = y.roundToInt(),
)
