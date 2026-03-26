package ru.tech.demomapapp.feature.map.render

import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.roundToInt
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.geojson.Point

private const val MAP_POINT_TAP_TOLERANCE_PX = 24f

internal class MapLibrePointClickAdapter(
    private val onPointClick: (RenderPointClick) -> Unit,
) {
    private var map: MapLibreMap? = null
    private var listener: MapLibreMap.OnMapClickListener? = null

    fun attach(map: MapLibreMap) {
        if (this.map === map && listener != null) {
            return
        }

        detach()

        val clickListener = MapLibreMap.OnMapClickListener { latLng ->
            map.toRenderPointClick(latLng)?.also(onPointClick) != null
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

private fun MapLibreMap.toRenderPointClick(latLng: LatLng): RenderPointClick? {
    val tapPoint = projection.toScreenLocation(latLng)
    val pointFeature = queryRenderedFeatures(
        RectF(
            tapPoint.x - MAP_POINT_TAP_TOLERANCE_PX,
            tapPoint.y - MAP_POINT_TAP_TOLERANCE_PX,
            tapPoint.x + MAP_POINT_TAP_TOLERANCE_PX,
            tapPoint.y + MAP_POINT_TAP_TOLERANCE_PX,
        ),
        MAP_POINTS_LAYER_ID,
    ).firstOrNull() ?: return null
    val pointKey = pointFeature.getStringProperty(MAP_POINT_KEY_PROPERTY).takeIf(String::isNotBlank) ?: return null
    val anchor = pointFeature.geometry()
        ?.let { geometry -> geometry as? Point }
        ?.toRenderPointAnchor(this)
        ?: tapPoint.toRenderPointAnchor()

    return RenderPointClick(
        pointKey = pointKey,
        anchor = anchor,
    )
}

private fun Point.toRenderPointAnchor(map: MapLibreMap): RenderPointAnchor =
    map.projection.toScreenLocation(LatLng(latitude(), longitude())).toRenderPointAnchor()

private fun PointF.toRenderPointAnchor(): RenderPointAnchor =
    RenderPointAnchor(
        screenX = x.roundToInt(),
        screenY = y.roundToInt(),
    )
