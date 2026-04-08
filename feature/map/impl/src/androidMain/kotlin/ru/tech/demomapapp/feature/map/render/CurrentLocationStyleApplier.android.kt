package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

internal fun Style.applyCurrentLocationMarker(marker: RenderCurrentLocationMarker?) {
    val featureCollection = FeatureCollection.fromFeatures(
        buildList {
            if (marker != null) {
                add(
                    Feature.fromGeometry(
                        Point.fromLngLat(marker.longitude, marker.latitude),
                    ),
                )
            }
        },
    )

    val source = getSourceAs<GeoJsonSource>(MAP_CURRENT_LOCATION_SOURCE_ID)
        ?: GeoJsonSource(MAP_CURRENT_LOCATION_SOURCE_ID, featureCollection).also(::addSource)
    source.setGeoJson(featureCollection)

    getLayer(MAP_CURRENT_LOCATION_LAYER_ID)?.let(::removeLayer)
    addLayer(
        CircleLayer(MAP_CURRENT_LOCATION_LAYER_ID, MAP_CURRENT_LOCATION_SOURCE_ID).withProperties(
            circleColor(if (marker?.isPlaceholder == true) "#FFD166" else "#2E86DE"),
            circleRadius(if (marker?.isPlaceholder == true) 8f else 9f),
            circleOpacity(0.95f),
            circleStrokeColor("#FFFFFF"),
            circleStrokeWidth(3f),
        ),
    )
}
