package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textAnchor
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textFont
import org.maplibre.android.style.layers.PropertyFactory.textHaloColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloWidth
import org.maplibre.android.style.layers.PropertyFactory.textOffset
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

internal fun Style.applyPoints(points: List<RenderMapPoint>) {
    val featureCollection = FeatureCollection.fromFeatures(
        points.map { point ->
            Feature.fromGeometry(
                Point.fromLngLat(point.longitude, point.latitude),
            ).apply {
                addStringProperty(MAP_FEATURE_KEY_PROPERTY, point.key)
                addStringProperty(MAP_FEATURE_TYPE_PROPERTY, RenderFeatureType.POINT.name)
                addStringProperty(MAP_POINT_LABEL_PROPERTY, point.label)
            }
        },
    )

    val source = getSourceAs<GeoJsonSource>(MAP_POINTS_SOURCE_ID)
        ?: GeoJsonSource(MAP_POINTS_SOURCE_ID, featureCollection).also(::addSource)
    source.setGeoJson(featureCollection)

    if (getLayer(MAP_POINTS_LAYER_ID) == null) {
        addLayer(
            CircleLayer(MAP_POINTS_LAYER_ID, MAP_POINTS_SOURCE_ID).withProperties(
                circleColor("#C65A2E"),
                circleRadius(8f),
                circleOpacity(0.95f),
                circleStrokeColor("#FFFDF8"),
                circleStrokeWidth(2.5f),
            ),
        )
    }

    if (getLayer(MAP_POINT_LABELS_LAYER_ID) == null) {
        addLayer(
            SymbolLayer(MAP_POINT_LABELS_LAYER_ID, MAP_POINTS_SOURCE_ID).withProperties(
                textField("{$MAP_POINT_LABEL_PROPERTY}"),
                textFont(arrayOf(MAP_LABEL_FONT_BOLD)),
                textSize(12f),
                textColor("#3D2B1F"),
                textHaloColor("#FFFDF8"),
                textHaloWidth(1.75f),
                textOffset(arrayOf(0f, 1.4f)),
                textAnchor("top"),
                textAllowOverlap(false),
            ),
        )
    }
}
