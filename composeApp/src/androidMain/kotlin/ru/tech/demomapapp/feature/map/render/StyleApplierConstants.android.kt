package ru.tech.demomapapp.feature.map.render

internal const val MAP_POINTS_SOURCE_ID = "map-renderer-points-source"
internal const val MAP_POINTS_LAYER_ID = "map-renderer-points-layer"
internal const val MAP_POINT_LABELS_LAYER_ID = "map-renderer-point-labels-layer"
internal const val MAP_LINES_SOURCE_ID = "map-renderer-lines-source"
internal const val MAP_LINES_LAYER_ID = "map-renderer-lines-layer"
internal const val MAP_LINE_LABELS_SOURCE_ID = "map-renderer-line-labels-source"
internal const val MAP_LINE_LABELS_LAYER_ID = "map-renderer-line-labels-layer"
internal const val MAP_POLYGONS_SOURCE_ID = "map-renderer-polygons-source"
internal const val MAP_POLYGONS_FILL_LAYER_ID = "map-renderer-polygons-fill-layer"
internal const val MAP_POLYGONS_OUTLINE_LAYER_ID = "map-renderer-polygons-outline-layer"
internal const val MAP_POLYGON_LABELS_SOURCE_ID = "map-renderer-polygon-labels-source"
internal const val MAP_POLYGON_LABELS_LAYER_ID = "map-renderer-polygon-labels-layer"
internal const val MAP_PREVIEW_LINE_SOURCE_ID = "map-renderer-preview-line-source"
internal const val MAP_PREVIEW_LINE_LAYER_ID = "map-renderer-preview-line-layer"
internal const val MAP_CURRENT_LOCATION_SOURCE_ID = "map-renderer-current-location-source"
internal const val MAP_CURRENT_LOCATION_LAYER_ID = "map-renderer-current-location-layer"
internal const val MAP_RULER_SOURCE_ID = "map-renderer-ruler-source"
internal const val MAP_RULER_LAYER_ID = "map-renderer-ruler-layer"
internal const val MAP_FIXED_LINE_SOURCE_ID = "map-renderer-fixed-line-source"
internal const val MAP_FIXED_LINE_LAYER_ID = "map-renderer-fixed-line-layer"
internal const val MAP_PREVIEW_POLYGON_SOURCE_ID = "map-renderer-preview-polygon-source"
internal const val MAP_PREVIEW_POLYGON_FILL_LAYER_ID = "map-renderer-preview-polygon-fill-layer"
internal const val MAP_FIXED_POLYGON_SOURCE_ID = "map-renderer-fixed-polygon-source"
internal const val MAP_FIXED_POLYGON_FILL_LAYER_ID = "map-renderer-fixed-polygon-fill-layer"
internal const val MAP_FIXED_POLYGON_OUTLINE_LAYER_ID = "map-renderer-fixed-polygon-outline-layer"
internal const val MAP_POINT_LABEL_PROPERTY = "label"
internal const val MAP_FEATURE_KEY_PROPERTY = "featureKey"
internal const val MAP_FEATURE_TYPE_PROPERTY = "featureType"
internal const val MAP_LABEL_FONT_REGULAR = "Noto Sans Regular"
internal const val MAP_LABEL_FONT_BOLD = "Noto Sans Bold"

internal val MAP_MANAGED_OVERLAY_LAYER_IDS = listOf(
    MAP_POINTS_LAYER_ID,
    MAP_POINT_LABELS_LAYER_ID,
    MAP_CURRENT_LOCATION_LAYER_ID,
    MAP_RULER_LAYER_ID,
    MAP_LINES_LAYER_ID,
    MAP_LINE_LABELS_LAYER_ID,
    MAP_POLYGONS_FILL_LAYER_ID,
    MAP_POLYGONS_OUTLINE_LAYER_ID,
    MAP_POLYGON_LABELS_LAYER_ID,
    MAP_FIXED_POLYGON_FILL_LAYER_ID,
    MAP_FIXED_POLYGON_OUTLINE_LAYER_ID,
    MAP_PREVIEW_POLYGON_FILL_LAYER_ID,
    MAP_FIXED_LINE_LAYER_ID,
    MAP_PREVIEW_LINE_LAYER_ID,
)
