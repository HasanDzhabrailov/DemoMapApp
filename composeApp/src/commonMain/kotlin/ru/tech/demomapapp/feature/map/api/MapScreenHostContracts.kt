package ru.tech.demomapapp.feature.map.api

@Suppress("TooManyFunctions")
interface MapScreenToolsHost {
    fun onMapToolsClick()
    fun onMapToolsDismiss()
    fun onAvailableMapsClick()
    fun onAvailableMapsDismiss()
    fun onAvailableMapSelect(mapId: String)
    fun onAvailableMapConfirm()
    fun onAvailableMapSelectionDismiss()
    fun onMapsOnScreenClick()
    fun onMapsOnScreenDismiss()
    fun onMapLayerActionsClick(layerId: String)
    fun onMapLayerActionsDismiss()
    fun onMoveLayerUpClick()
    fun onMoveLayerDownClick()
    fun onRemoveLayerClick()
    fun onLayerOpacityClick()
    fun onLayerOpacityChange(value: Float)
    fun onLayerOpacityDismiss()
}

interface MapScreenLocationHost {
    fun onGpsToggle()
    fun onMyLocationClick()
    fun onCurrentLocationFocusClick()
    fun onLocationRequestConsumed()
    fun onLocationResult(result: LocationRequestResult)
}

@Suppress("TooManyFunctions")
interface MapScreenViewportHost {
    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onZoomInClick()
    fun onZoomOutClick()
    fun onViewportCommandConsumed()
    fun onRulerToggle()
    fun onCenterMarkerClick()
    fun onCenterMarkerMenuDismiss()
    fun onCreatePointClick()
    fun onCreateLineClick()
    fun onCreatePolygonClick()
}

@Suppress("TooManyFunctions")
interface MapScreenDrawingHost {
    fun onCreatePointLatitudeChange(value: String)
    fun onCreatePointLongitudeChange(value: String)
    fun onCreatePointTitleChange(value: String)
    fun onCreatePointConfirm()
    fun onCreatePointSheetDismiss()
    fun onDrawingAddPositionClick()
    fun onDrawingRemoveLastPositionClick()
    fun onDrawingDetailsClick()
    fun onDrawingDismiss()
    fun onCreateShapeTitleChange(value: String)
    fun onCreateShapeConfirm()
    fun onCreateShapeSheetDismiss()
}

interface MapScreenFeatureHost {
    fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    )

    fun onFeatureInfoWindowDismiss()
}
