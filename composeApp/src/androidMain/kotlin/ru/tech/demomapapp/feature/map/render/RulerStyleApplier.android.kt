package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineCap
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineJoin
import org.maplibre.android.style.layers.PropertyFactory.lineOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.maps.Style
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

internal fun Style.applyRulerMeasurement(measurement: RenderRulerMeasurement?) {
    val featureCollection = FeatureCollection.fromFeatures(
        buildList {
            if (measurement != null) {
                add(
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            listOf(
                                Point.fromLngLat(measurement.startLongitude, measurement.startLatitude),
                                Point.fromLngLat(measurement.endLongitude, measurement.endLatitude),
                            ),
                        ),
                    ),
                )

                measurement.arrowSegments.forEach { arrowSegment ->
                    add(
                        Feature.fromGeometry(
                            LineString.fromLngLats(
                                listOf(
                                    Point.fromLngLat(arrowSegment.startLongitude, arrowSegment.startLatitude),
                                    Point.fromLngLat(arrowSegment.endLongitude, arrowSegment.endLatitude),
                                ),
                            ),
                        ),
                    )
                }
            }
        },
    )

    val source = getSourceAs<GeoJsonSource>(MAP_RULER_SOURCE_ID)
        ?: GeoJsonSource(MAP_RULER_SOURCE_ID, featureCollection).also(::addSource)
    source.setGeoJson(featureCollection)

    if (getLayer(MAP_RULER_LAYER_ID) == null) {
        addLayer(
            LineLayer(MAP_RULER_LAYER_ID, MAP_RULER_SOURCE_ID).withProperties(
                lineColor("#1D4ED8"),
                lineWidth(4f),
                lineOpacity(0.98f),
                lineJoin("round"),
                lineCap("round"),
            ),
        )
    }
}