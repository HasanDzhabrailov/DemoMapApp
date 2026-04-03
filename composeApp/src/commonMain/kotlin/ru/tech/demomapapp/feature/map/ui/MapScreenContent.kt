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
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.mapscreen.MapScreenUiComponent
import ru.tech.demomapapp.feature.map.mapscreen.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer
import ru.tech.demomapapp.feature.map.tools.ui.ToolsOverlay

@Composable
fun MapScreenContent(component: MapScreenComponent, modifier: Modifier = Modifier) {
    val uiComponent = component as? MapScreenUiComponent
        ?: error("MapScreenContent requires MapScreenUiComponent")
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

        MapToolsButton(
            onClick = component::onMapToolsClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(16.dp),
        )

        LocationControls(
            component = uiComponent.locationComponent,
            modifier = Modifier.align(Alignment.BottomStart),
        )

        ViewportControls(
            component = uiComponent.viewportComponent,
            onCenterMarkerClick = component::onCenterMarkerClick,
            onCreatePointClick = component::onCreatePointClick,
            onCreateLineClick = component::onCreateLineClick,
            onCreatePolygonClick = component::onCreatePolygonClick,
        )

        DrawingContent(component = uiComponent.drawingComponent)

        RulerOverlay(component = uiComponent.rulerComponent)

        ToolsOverlay(
            component = uiComponent.toolsComponent,
            isGpsEnabled = model.isGpsToggleChecked(),
            isRulerEnabled = model.isRulerEnabled,
            onDismiss = component::onMapToolsDismiss,
            onGpsToggle = component::onGpsToggle,
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
