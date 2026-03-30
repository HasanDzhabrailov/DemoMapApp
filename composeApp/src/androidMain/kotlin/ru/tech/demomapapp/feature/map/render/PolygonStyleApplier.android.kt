package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineCap
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineJoin
import org.maplibre.android.style.layers.PropertyFactory.lineOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textAnchor
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textFont
import org.maplibre.android.style.layers.PropertyFactory.textHaloColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloWidth
import org.maplibre.android.style.layers.PropertyFactory.textOffset
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.maps.Style
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon

internal fun Style.applyPolygons(polygons: List<RenderMapPolygon>) {
    val polygonFeatures = FeatureCollection.fromFeatures(
        polygons.map { polygon ->
            Feature.fromGeometry(
                Polygon.fromLngLats(listOf(polygon.vertices.closedRing())),
            ).apply {
                addStringProperty(MAP_FEATURE_KEY_PROPERTY, polygon.key)
                addStringProperty(MAP_FEATURE_TYPE_PROPERTY, RenderFeatureType.POLYGON.name)
                addStringProperty(MAP_POINT_LABEL_PROPERTY, polygon.label)
            }
        },
    )
    val labelFeatures = FeatureCollection.fromFeatures(
        polygons.map { polygon ->
            Feature.fromGeometry(Point.fromLngLat(polygon.labelLongitude, polygon.labelLatitude)).apply {
                addStringProperty(MAP_POINT_LABEL_PROPERTY, polygon.label)
            }
        },
    )

    val polygonSource = getSourceAs<GeoJsonSource>(MAP_POLYGONS_SOURCE_ID)
        ?: GeoJsonSource(MAP_POLYGONS_SOURCE_ID, polygonFeatures).also(::addSource)
    polygonSource.setGeoJson(polygonFeatures)
    val labelSource = getSourceAs<GeoJsonSource>(MAP_POLYGON_LABELS_SOURCE_ID)
        ?: GeoJsonSource(MAP_POLYGON_LABELS_SOURCE_ID, labelFeatures).also(::addSource)
    labelSource.setGeoJson(labelFeatures)

    if (getLayer(MAP_POLYGONS_FILL_LAYER_ID) == null) {
        addLayer(
            FillLayer(MAP_POLYGONS_FILL_LAYER_ID, MAP_POLYGONS_SOURCE_ID).withProperties(
                fillColor("#D97745"),
                fillOpacity(0.22f),
            ),
        )
    }

    if (getLayer(MAP_POLYGONS_OUTLINE_LAYER_ID) == null) {
        addLayer(
            LineLayer(MAP_POLYGONS_OUTLINE_LAYER_ID, MAP_POLYGONS_SOURCE_ID).withProperties(
                lineColor("#6E2F1A"),
                lineWidth(3f),
                lineOpacity(0.95f),
                lineJoin("round"),
                lineCap("round"),
            ),
        )
    }

    if (getLayer(MAP_POLYGON_LABELS_LAYER_ID) == null) {
        addLayer(
            SymbolLayer(MAP_POLYGON_LABELS_LAYER_ID, MAP_POLYGON_LABELS_SOURCE_ID).withProperties(
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

internal fun List<RenderMapVertex>.closedRing(): List<Point> {
    val points = map(RenderMapVertex::toGeometryPoint)
    return if (points.firstOrNull() == points.lastOrNull()) {
        points
    } else {
        points + points.first()
    }
}