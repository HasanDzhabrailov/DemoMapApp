package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

@Composable
internal fun BoxScope.MapScreenOverlays(
    model: MapScreenComponent.Model,
    onMapToolsClick: () -> Unit,
    onMapToolsDismiss: () -> Unit,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    onAvailableMapsClick: () -> Unit,
    onAvailableMapsDismiss: () -> Unit,
    onAvailableMapSelect: (String) -> Unit,
    onAvailableMapConfirm: () -> Unit,
    onAvailableMapSelectionDismiss: () -> Unit,
    onMapsOnScreenClick: () -> Unit,
    onMapsOnScreenDismiss: () -> Unit,
    onMapLayerActionsClick: (String) -> Unit,
    onMapLayerActionsDismiss: () -> Unit,
    onMoveLayerUpClick: () -> Unit,
    onMoveLayerDownClick: () -> Unit,
    onRemoveLayerClick: () -> Unit,
    onLayerOpacityClick: () -> Unit,
    onLayerOpacityChange: (Float) -> Unit,
    onLayerOpacityDismiss: () -> Unit,
    onGpsToggle: () -> Unit,
    onMyLocationClick: () -> Unit,
    onCurrentLocationFocusClick: () -> Unit,
    onRulerToggle: () -> Unit,
    onCenterMarkerClick: () -> Unit,
    onCenterMarkerMenuDismiss: () -> Unit,
    onCreatePointClick: () -> Unit,
    onCreateLineClick: () -> Unit,
    onCreatePolygonClick: () -> Unit,
    onCreatePointLatitudeChange: (String) -> Unit,
    onCreatePointLongitudeChange: (String) -> Unit,
    onCreatePointTitleChange: (String) -> Unit,
    onCreatePointConfirm: () -> Unit,
    onCreatePointSheetDismiss: () -> Unit,
    onDrawingAddPositionClick: () -> Unit,
    onDrawingRemoveLastPositionClick: () -> Unit,
    onDrawingDetailsClick: () -> Unit,
    onDrawingDismiss: () -> Unit,
    onCreateShapeTitleChange: (String) -> Unit,
    onCreateShapeConfirm: () -> Unit,
    onCreateShapeSheetDismiss: () -> Unit,
    onFeatureInfoWindowDismiss: () -> Unit,
) {
    MapLeftControlsOverlay(
        onMyLocationClick = onMyLocationClick,
        isMyLocationEnabled = model.isManualLocationEnabled(),
        onCurrentLocationFocusClick = onCurrentLocationFocusClick,
        isCurrentLocationFocusEnabled = model.isCurrentLocationFocusEnabled(),
        onMapToolsClick = onMapToolsClick,
        modifier = Modifier.align(Alignment.BottomStart),
    )

    MapZoomControlsOverlay(
        onZoomInClick = onZoomInClick,
        onZoomOutClick = onZoomOutClick,
        modifier = Modifier.align(Alignment.BottomEnd),
    )

    if (model.isMapToolsMenuVisible) {
        MapToolsMenuOverlay(
            isGpsEnabled = model.isGpsToggleChecked(),
            isRulerEnabled = model.isRulerEnabled,
            onDismiss = onMapToolsDismiss,
            onAvailableMapsClick = onAvailableMapsClick,
            onMapsOnScreenClick = onMapsOnScreenClick,
            onGpsToggle = onGpsToggle,
            onRulerToggle = onRulerToggle,
        )
    }

    if (model.isAvailableMapsSheetVisible) {
        AvailableMapsBottomSheet(
            items = model.availableMapCatalog,
            onSelect = onAvailableMapSelect,
            onDismiss = onAvailableMapsDismiss,
        )
    }

    model.selectedAvailableMap?.let { selectedMap ->
        ConfirmAddMapDialog(
            mapTitle = selectedMap.title,
            onConfirm = onAvailableMapConfirm,
            onDismiss = onAvailableMapSelectionDismiss,
        )
    }

    if (model.isMapsOnScreenSheetVisible) {
        MapsOnScreenBottomSheet(
            baseMapTitle = model.mapState.style.displayName(),
            layers = model.mapState.overlayLayers,
            onLayerActionsClick = onMapLayerActionsClick,
            onDismiss = onMapsOnScreenDismiss,
        )
    }

    model.selectedOverlayLayer?.let { selectedLayer ->
        val visibleLayers = model.mapState.overlayLayers.asReversed()
        val selectedLayerIndex = visibleLayers.indexOfFirst { it.id == selectedLayer.id }
        LayerActionsDialog(
            layer = selectedLayer,
            canMoveUp = selectedLayerIndex > 0,
            canMoveDown = selectedLayerIndex in 0 until visibleLayers.lastIndex,
            onMoveUp = onMoveLayerUpClick,
            onMoveDown = onMoveLayerDownClick,
            onChangeOpacity = onLayerOpacityClick,
            onRemove = onRemoveLayerClick,
            onDismiss = onMapLayerActionsDismiss,
        )
    }

    model.editingOverlayOpacityLayer?.let { selectedLayer ->
        LayerOpacityBottomSheet(
            layerTitle = selectedLayer.title,
            opacity = selectedLayer.opacity,
            onOpacityChange = onLayerOpacityChange,
            onDismiss = onLayerOpacityDismiss,
        )
    }

    model.rulerInfoWindow?.let { infoWindow ->
        RulerInfoWindowOverlay(
            state = infoWindow,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-72).dp),
        )
    }

    CenterMarker(
        onClick = onCenterMarkerClick,
        modifier = Modifier.align(Alignment.Center),
    )

    if (model.isCenterMarkerMenuVisible) {
        CenterMarkerMenuOverlay(
            onDismiss = onCenterMarkerMenuDismiss,
            onCreatePointClick = onCreatePointClick,
            onCreateLineClick = onCreateLineClick,
            onCreatePolygonClick = onCreatePolygonClick,
        )
    }

    if (model.isCreatePointSheetVisible) {
        model.createPointDraft?.let { draft ->
            CreatePointBottomSheet(
                draft = draft,
                onLatitudeChange = onCreatePointLatitudeChange,
                onLongitudeChange = onCreatePointLongitudeChange,
                onTitleChange = onCreatePointTitleChange,
                onConfirm = onCreatePointConfirm,
                onDismiss = onCreatePointSheetDismiss,
            )
        }
    }

    model.shapeDrawingDraft?.let { draft ->
        ShapeDrawingControlsOverlay(
            mode = draft.mode,
            fixedVertexCount = draft.fixedVertices.size,
            onRemoveLastClick = onDrawingRemoveLastPositionClick,
            onAddPositionClick = onDrawingAddPositionClick,
            onDetailsClick = onDrawingDetailsClick,
            onDismissClick = onDrawingDismiss,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (model.isCreateShapeSheetVisible) {
        model.shapeDrawingDraft?.let { draft ->
            CreateShapeBottomSheet(
                draft = draft,
                onTitleChange = onCreateShapeTitleChange,
                onConfirm = onCreateShapeConfirm,
                onDismiss = onCreateShapeSheetDismiss,
            )
        }
    }

    model.selectedFeatureInfoWindow?.let { infoWindow ->
        PointInfoWindowOverlay(
            state = infoWindow,
            onDismiss = onFeatureInfoWindowDismiss,
        )
    }
}

private fun ru.tech.demomapapp.feature.map.api.MapStyle.displayName(): String = when (this) {
    ru.tech.demomapapp.feature.map.api.MapStyle.DEMO -> "DEM Map"
    ru.tech.demomapapp.feature.map.api.MapStyle.OPEN_STREET_MAP -> "OpenStreetMap"
}
