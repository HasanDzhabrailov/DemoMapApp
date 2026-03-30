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
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.maps.Style
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Polygon

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