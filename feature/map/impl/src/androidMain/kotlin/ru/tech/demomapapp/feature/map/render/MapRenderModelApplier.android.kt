package ru.tech.demomapapp.feature.map.render

import ru.tech.demomapapp.map.MapViewHolder

internal class MapRenderModelApplier {

    suspend fun apply(holder: MapViewHolder, model: MapRenderModel) {
        if (holder.isDestroyed) {
            return
        }

        val style = holder.loadStyle(model.style.styleUrl())
        style.applyRasterTileLayers(model.tileLayers)
        style.applyPoints(model.points)
        style.applyCurrentLocationMarker(model.currentLocationMarker)
        style.applyRulerMeasurement(model.rulerMeasurement)
        style.applyLines(model.lines)
        style.applyPolygons(model.polygons)
        style.applyDrawingPreview(model.drawingPreview)
    }
}
