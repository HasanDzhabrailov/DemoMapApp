package ru.tech.demomapapp.feature.map.render

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style

internal class MapRenderModelApplier {
    private var lastAppliedStyle: RenderMapStyle? = null

    suspend fun apply(
        holder: MapViewHolder,
        model: MapRenderModel,
    ) {
        if (holder.isDestroyed) {
            return
        }

        val map = holder.awaitMap()
        val style = map.loadStyle(model.style, lastAppliedStyle)
        lastAppliedStyle = model.style
        style.applyPoints(model.points)
        style.applyCurrentLocationMarker(model.currentLocationMarker)
        style.applyRulerMeasurement(model.rulerMeasurement)
        style.applyLines(model.lines)
        style.applyPolygons(model.polygons)
        style.applyDrawingPreview(model.drawingPreview)
    }
}

private suspend fun MapLibreMap.loadStyle(
    style: RenderMapStyle,
    lastAppliedStyle: RenderMapStyle?,
): Style {
    val currentStyle = this.style
    if (lastAppliedStyle == style && currentStyle != null) {
        return currentStyle
    }

    return suspendCancellableCoroutine { continuation ->
        setStyle(style.styleUrl()) { loadedStyle ->
            if (continuation.isActive) {
                continuation.resume(loadedStyle)
            }
        }
    }
}
