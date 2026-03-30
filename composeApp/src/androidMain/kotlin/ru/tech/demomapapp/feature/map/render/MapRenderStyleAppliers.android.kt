package ru.tech.demomapapp.feature.map.render

import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
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
import org.maplibre.android.maps.Style
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon

private const val MAP_POINTS_SOURCE_ID = "map-renderer-points-source"
internal const val MAP_POINTS_LAYER_ID = "map-renderer-points-layer"
private const val MAP_POINT_LABELS_LAYER_ID = "map-renderer-point-labels-layer"
private const val MAP_LINES_SOURCE_ID = "map-renderer-lines-source"
internal const val MAP_LINES_LAYER_ID = "map-renderer-lines-layer"
private const val MAP_LINE_LABELS_SOURCE_ID = "map-renderer-line-labels-source"
private const val MAP_LINE_LABELS_LAYER_ID = "map-renderer-line-labels-layer"
private const val MAP_POLYGONS_SOURCE_ID = "map-renderer-polygons-source"
internal const val MAP_POLYGONS_FILL_LAYER_ID = "map-renderer-polygons-fill-layer"
internal const val MAP_POLYGONS_OUTLINE_LAYER_ID = "map-renderer-polygons-outline-layer"
private const val MAP_POLYGON_LABELS_SOURCE_ID = "map-renderer-polygon-labels-source"
private const val MAP_POLYGON_LABELS_LAYER_ID = "map-renderer-polygon-labels-layer"
private const val MAP_PREVIEW_LINE_SOURCE_ID = "map-renderer-preview-line-source"
private const val MAP_PREVIEW_LINE_LAYER_ID = "map-renderer-preview-line-layer"
private const val MAP_CURRENT_LOCATION_SOURCE_ID = "map-renderer-current-location-source"
private const val MAP_CURRENT_LOCATION_LAYER_ID = "map-renderer-current-location-layer"
private const val MAP_RULER_SOURCE_ID = "map-renderer-ruler-source"
private const val MAP_RULER_LAYER_ID = "map-renderer-ruler-layer"
private const val MAP_FIXED_LINE_SOURCE_ID = "map-renderer-fixed-line-source"
private const val MAP_FIXED_LINE_LAYER_ID = "map-renderer-fixed-line-layer"
private const val MAP_PREVIEW_POLYGON_SOURCE_ID = "map-renderer-preview-polygon-source"
private const val MAP_PREVIEW_POLYGON_FILL_LAYER_ID = "map-renderer-preview-polygon-fill-layer"
private const val MAP_FIXED_POLYGON_SOURCE_ID = "map-renderer-fixed-polygon-source"
private const val MAP_FIXED_POLYGON_FILL_LAYER_ID = "map-renderer-fixed-polygon-fill-layer"
private const val MAP_FIXED_POLYGON_OUTLINE_LAYER_ID = "map-renderer-fixed-polygon-outline-layer"
private const val MAP_POINT_LABEL_PROPERTY = "label"
internal const val MAP_FEATURE_KEY_PROPERTY = "featureKey"
internal const val MAP_FEATURE_TYPE_PROPERTY = "featureType"
private const val MAP_LABEL_FONT_REGULAR = "Noto Sans Regular"
private const val MAP_LABEL_FONT_BOLD = "Noto Sans Bold"

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

internal fun Style.applyDrawingPreview(preview: RenderDrawingPreview?) {
    val fixedLineFeatureCollection = FeatureCollection.fromFeatures(
        buildList {
            if (preview != null && preview.fixedLineVertices.size >= 2) {
                add(
                    Feature.fromGeometry(
                        LineString.fromLngLats(preview.fixedLineVertices.map(RenderMapVertex::toGeometryPoint)),
                    ),
                )
            }
        },
    )
    val previewLineFeatureCollection = FeatureCollection.fromFeatures(
        buildList {
            if (preview != null && preview.previewLineVertices.size >= 2) {
                add(
                    Feature.fromGeometry(
                        LineString.fromLngLats(preview.previewLineVertices.map(RenderMapVertex::toGeometryPoint)),
                    ),
                )
            }
        },
    )
    val fixedPolygonFeatureCollection = FeatureCollection.fromFeatures(
        buildList {
            if (preview != null && preview.fixedPolygonVertices.size >= 3) {
                add(
                    Feature.fromGeometry(
                        Polygon.fromLngLats(listOf(preview.fixedPolygonVertices.closedRing())),
                    ),
                )
            }
        },
    )
    val previewPolygonFeatureCollection = FeatureCollection.fromFeatures(emptyList())

    val fixedLineSource = getSourceAs<GeoJsonSource>(MAP_FIXED_LINE_SOURCE_ID)
        ?: GeoJsonSource(MAP_FIXED_LINE_SOURCE_ID, fixedLineFeatureCollection).also(::addSource)
    fixedLineSource.setGeoJson(fixedLineFeatureCollection)

    val previewLineSource = getSourceAs<GeoJsonSource>(MAP_PREVIEW_LINE_SOURCE_ID)
        ?: GeoJsonSource(MAP_PREVIEW_LINE_SOURCE_ID, previewLineFeatureCollection).also(::addSource)
    previewLineSource.setGeoJson(previewLineFeatureCollection)

    val fixedPolygonSource = getSourceAs<GeoJsonSource>(MAP_FIXED_POLYGON_SOURCE_ID)
        ?: GeoJsonSource(MAP_FIXED_POLYGON_SOURCE_ID, fixedPolygonFeatureCollection).also(::addSource)
    fixedPolygonSource.setGeoJson(fixedPolygonFeatureCollection)

    val previewPolygonSource = getSourceAs<GeoJsonSource>(MAP_PREVIEW_POLYGON_SOURCE_ID)
        ?: GeoJsonSource(MAP_PREVIEW_POLYGON_SOURCE_ID, previewPolygonFeatureCollection).also(::addSource)
    previewPolygonSource.setGeoJson(previewPolygonFeatureCollection)

    if (getLayer(MAP_FIXED_POLYGON_FILL_LAYER_ID) == null) {
        addLayer(
            FillLayer(MAP_FIXED_POLYGON_FILL_LAYER_ID, MAP_FIXED_POLYGON_SOURCE_ID).withProperties(
                fillColor("#EC407A"),
                fillOpacity(0.2f),
            ),
        )
    }

    if (getLayer(MAP_FIXED_POLYGON_OUTLINE_LAYER_ID) == null) {
        addLayer(
            LineLayer(MAP_FIXED_POLYGON_OUTLINE_LAYER_ID, MAP_FIXED_POLYGON_SOURCE_ID).withProperties(
                lineColor("#111111"),
                lineWidth(3f),
                lineOpacity(0.92f),
                lineJoin("round"),
                lineCap("round"),
            ),
        )
    }

    if (getLayer(MAP_PREVIEW_POLYGON_FILL_LAYER_ID) == null) {
        addLayer(
            FillLayer(MAP_PREVIEW_POLYGON_FILL_LAYER_ID, MAP_PREVIEW_POLYGON_SOURCE_ID).withProperties(
                fillColor("#EC407A"),
                fillOpacity(0f),
            ),
        )
    }

    if (getLayer(MAP_FIXED_LINE_LAYER_ID) == null) {
        addLayer(
            LineLayer(MAP_FIXED_LINE_LAYER_ID, MAP_FIXED_LINE_SOURCE_ID).withProperties(
                lineColor("#EC407A"),
                lineWidth(4f),
                lineOpacity(0.92f),
                lineJoin("round"),
                lineCap("round"),
            ),
        )
    }

    if (getLayer(MAP_PREVIEW_LINE_LAYER_ID) == null) {
        addLayer(
            LineLayer(MAP_PREVIEW_LINE_LAYER_ID, MAP_PREVIEW_LINE_SOURCE_ID).withProperties(
                lineColor("#111111"),
                lineWidth(4f),
                lineOpacity(0.92f),
                lineJoin("round"),
                lineCap("round"),
            ),
        )
    }
}

private fun RenderMapVertex.toGeometryPoint(): Point = Point.fromLngLat(longitude, latitude)

private fun List<RenderMapVertex>.closedRing(): List<Point> {
    val points = map(RenderMapVertex::toGeometryPoint)
    return if (points.firstOrNull() == points.lastOrNull()) {
        points
    } else {
        points + points.first()
    }
}

internal fun RenderMapStyle.styleUrl(): String =
    when (this) {
        RenderMapStyle.DEFAULT -> "https://tiles.openfreemap.org/styles/liberty"
    }
