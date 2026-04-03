package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer

@Composable
fun MapScreenContent(component: MapScreenComponent, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()
    val renderModel = model.mapState.toRenderModel(
        shapeDrawingDraft = model.shapeDrawingDraft,
        currentSnapshot = model.lastCameraSnapshot,
        currentLocationMarker = model.currentLocationMarker,
        rulerMeasurement = model.rulerMeasurement,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        MapRenderer(
            model = renderModel,
            modifier = Modifier.fillMaxSize(),
            viewportCommand = model.pendingViewportCommand,
            onCameraIdle = component::onCameraIdle,
            onViewportCommandConsumed = component::onViewportCommandConsumed,
            onFeatureClick = { click ->
                component.onFeatureClick(
                    featureKey = click.featureKey,
                    featureType = click.toFeatureType(),
                    anchor = click.toFeatureInfoWindowAnchor(),
                )
            },
        )

        MapLocationEffectBinder(
            request = model.pendingLocationRequest,
            onRequestConsumed = component::onLocationRequestConsumed,
            onLocationResult = component::onLocationResult,
        )

        MapScreenOverlays(
            model = model,
            onMapToolsClick = component::onMapToolsClick,
            onMapToolsDismiss = component::onMapToolsDismiss,
            onZoomInClick = component::onZoomInClick,
            onZoomOutClick = component::onZoomOutClick,
            onAvailableMapsClick = component::onAvailableMapsClick,
            onAvailableMapsDismiss = component::onAvailableMapsDismiss,
            onAvailableMapSelect = component::onAvailableMapSelect,
            onAvailableMapConfirm = component::onAvailableMapConfirm,
            onAvailableMapSelectionDismiss = component::onAvailableMapSelectionDismiss,
            onMapsOnScreenClick = component::onMapsOnScreenClick,
            onMapsOnScreenDismiss = component::onMapsOnScreenDismiss,
            onMapLayerActionsClick = component::onMapLayerActionsClick,
            onMapLayerActionsDismiss = component::onMapLayerActionsDismiss,
            onMoveLayerUpClick = component::onMoveLayerUpClick,
            onMoveLayerDownClick = component::onMoveLayerDownClick,
            onRemoveLayerClick = component::onRemoveLayerClick,
            onLayerOpacityClick = component::onLayerOpacityClick,
            onLayerOpacityChange = component::onLayerOpacityChange,
            onLayerOpacityDismiss = component::onLayerOpacityDismiss,
            onGpsToggle = component::onGpsToggle,
            onMyLocationClick = component::onMyLocationClick,
            onCurrentLocationFocusClick = component::onCurrentLocationFocusClick,
            onRulerToggle = component::onRulerToggle,
            onCenterMarkerClick = component::onCenterMarkerClick,
            onCenterMarkerMenuDismiss = component::onCenterMarkerMenuDismiss,
            onCreatePointClick = component::onCreatePointClick,
            onCreateLineClick = component::onCreateLineClick,
            onCreatePolygonClick = component::onCreatePolygonClick,
            onCreatePointLatitudeChange = component::onCreatePointLatitudeChange,
            onCreatePointLongitudeChange = component::onCreatePointLongitudeChange,
            onCreatePointTitleChange = component::onCreatePointTitleChange,
            onCreatePointConfirm = component::onCreatePointConfirm,
            onCreatePointSheetDismiss = component::onCreatePointSheetDismiss,
            onDrawingAddPositionClick = component::onDrawingAddPositionClick,
            onDrawingRemoveLastPositionClick = component::onDrawingRemoveLastPositionClick,
            onDrawingDetailsClick = component::onDrawingDetailsClick,
            onDrawingDismiss = component::onDrawingDismiss,
            onCreateShapeTitleChange = component::onCreateShapeTitleChange,
            onCreateShapeConfirm = component::onCreateShapeConfirm,
            onCreateShapeSheetDismiss = component::onCreateShapeSheetDismiss,
            onFeatureInfoWindowDismiss = component::onFeatureInfoWindowDismiss,
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun BoxScope.MapScreenOverlays(
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
