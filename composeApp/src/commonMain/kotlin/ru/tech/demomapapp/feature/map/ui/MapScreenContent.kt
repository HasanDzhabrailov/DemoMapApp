package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer

@Composable
fun MapScreenContent(
    component: MapScreenComponent,
    modifier: Modifier = Modifier,
) {
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
            .background(MaterialTheme.colorScheme.surface)
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
            onMapsOnScreenClick = component::onMapsOnScreenClick,
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