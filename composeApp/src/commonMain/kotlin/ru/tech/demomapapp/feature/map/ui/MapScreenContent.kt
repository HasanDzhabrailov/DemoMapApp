package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.MapScreenUiContract
import ru.tech.demomapapp.feature.map.drawing.ui.DrawingContent
import ru.tech.demomapapp.feature.map.location.ui.LocationControls
import ru.tech.demomapapp.feature.map.mapscreen.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer
import ru.tech.demomapapp.feature.map.ruler.ui.RulerOverlay
import ru.tech.demomapapp.feature.map.tools.ui.ToolsOverlay
import ru.tech.demomapapp.feature.map.viewport.ui.ViewportControls

@Composable
fun MapScreenContent(component: MapScreenUiContract, modifier: Modifier = Modifier) {
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
            onViewportCommandConsumed = component.viewportUi::onViewportCommandConsumed,
            onFeatureClick = { click ->
                component.onFeatureClick(
                    featureKey = click.featureKey,
                    featureType = click.toFeatureType(),
                    anchor = click.toFeatureInfoWindowAnchor(),
                )
            },
        )

        MapToolsButton(
            onClick = component::onMapToolsClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(16.dp),
        )

        LocationControls(
            component = component.locationUi,
            modifier = Modifier.align(Alignment.BottomStart),
        )

        ViewportControls(
            isCenterMarkerMenuVisible = model.isCenterMarkerMenuVisible,
            isCenterMarkerEnabled = model.isCenterMarkerEnabled,
            onZoomInClick = component.viewportUi::onZoomInClick,
            onZoomOutClick = component.viewportUi::onZoomOutClick,
            onCenterMarkerMenuDismiss = component.viewportUi::onCenterMarkerMenuDismiss,
            onCenterMarkerClick = component::onCenterMarkerClick,
            onCreatePointClick = component::onCreatePointClick,
            onCreateLineClick = component::onCreateLineClick,
            onCreatePolygonClick = component::onCreatePolygonClick,
        )

        DrawingContent(component = component.drawingUi)

        RulerOverlay(component = component.rulerUi)

        ToolsOverlay(
            component = component.toolsUi,
            isGpsEnabled = model.isGpsToggleChecked(),
            isRulerEnabled = model.isRulerEnabled,
            onDismiss = component.toolsUi::onMapToolsDismiss,
            onGpsToggle = component.locationUi::onGpsToggle,
            onRulerToggle = component::onRulerToggle,
        )

        model.selectedFeatureInfoWindow?.let { infoWindow ->
            PointInfoWindowOverlay(
                state = infoWindow,
                onDismiss = component::onFeatureInfoWindowDismiss,
            )
        }
    }
}
