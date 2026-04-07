package ru.tech.demomapapp.feature.map.api

interface MapScreenToolsHost {
    fun onMapToolsClick()
    fun onAvailableMapsClick()
    fun onMapsOnScreenClick()
}

interface MapScreenLocationHost

interface MapScreenViewportHost {
    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onViewportCommandConsumed()
    fun onCenterMarkerClick()
    fun onRulerToggle()
}

interface MapScreenDrawingHost {
    fun onCreatePointClick()
    fun onCreateLineClick()
    fun onCreatePolygonClick()
    fun onDrawingAddPositionClick()
}

interface MapScreenFeatureHost {
    /**
     * Feature selection with render data passed explicitly.
     * Render data (points, lines, polygons) is owned by child components.
     */
    fun onFeatureClick(
        points: List<MapPoint>,
        lines: List<MapLine>,
        polygons: List<MapPolygon>,
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    )

    fun onFeatureInfoWindowDismiss()
}
