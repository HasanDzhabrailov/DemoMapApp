package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineCap
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineJoin
import org.maplibre.android.style.layers.PropertyFactory.lineOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
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
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

internal fun Style.applyLines(lines: List<RenderMapLine>) {
    val lineFeatures = FeatureCollection.fromFeatures(
        lines.map { line ->
            Feature.fromGeometry(
                LineString.fromLngLats(line.vertices.map(RenderMapVertex::toGeometryPoint)),
            ).apply {
                addStringProperty(MAP_FEATURE_KEY_PROPERTY, line.key)
                addStringProperty(MAP_FEATURE_TYPE_PROPERTY, RenderFeatureType.LINE.name)
                addStringProperty(MAP_POINT_LABEL_PROPERTY, line.label)
            }
        },
    )
    val labelFeatures = FeatureCollection.fromFeatures(
        lines.map { line ->
            Feature.fromGeometry(Point.fromLngLat(line.labelLongitude, line.labelLatitude)).apply {
                addStringProperty(MAP_POINT_LABEL_PROPERTY, line.label)
            }
        },
    )

    val lineSource = getSourceAs<GeoJsonSource>(MAP_LINES_SOURCE_ID)
        ?: GeoJsonSource(MAP_LINES_SOURCE_ID, lineFeatures).also(::addSource)
    lineSource.setGeoJson(lineFeatures)
    val labelSource = getSourceAs<GeoJsonSource>(MAP_LINE_LABELS_SOURCE_ID)
        ?: GeoJsonSource(MAP_LINE_LABELS_SOURCE_ID, labelFeatures).also(::addSource)
    labelSource.setGeoJson(labelFeatures)

    if (getLayer(MAP_LINES_LAYER_ID) == null) {
        addLayer(
            LineLayer(MAP_LINES_LAYER_ID, MAP_LINES_SOURCE_ID).withProperties(
                lineColor("#A63D40"),
                lineWidth(4f),
                lineOpacity(0.95f),
                lineJoin("round"),
                lineCap("round"),
            ),
        )
    }

    if (getLayer(MAP_LINE_LABELS_LAYER_ID) == null) {
        addLayer(
            SymbolLayer(MAP_LINE_LABELS_LAYER_ID, MAP_LINE_LABELS_SOURCE_ID).withProperties(
                textField("{$MAP_POINT_LABEL_PROPERTY}"),
                textFont(arrayOf(MAP_LABEL_FONT_REGULAR)),
                textSize(12f),
                textColor("#3D2B1F"),
                textHaloColor("#FFFDF8"),
                textHaloWidth(1.75f),
                textOffset(arrayOf(0f, 1.2f)),
                textAnchor("top"),
                textAllowOverlap(false),
            ),
        )
    }
}

internal fun RenderMapVertex.toGeometryPoint(): Point = Point.fromLngLat(longitude, latitude)
