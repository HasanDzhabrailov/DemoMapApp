package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.DrawingModel
import ru.tech.demomapapp.feature.map.api.LocationModel
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapScreenUiContract
import ru.tech.demomapapp.feature.map.api.RulerModel
import ru.tech.demomapapp.feature.map.api.ToolsModel
import ru.tech.demomapapp.feature.map.api.ViewportModel
import ru.tech.demomapapp.feature.map.drawing.ui.DrawingContent
import ru.tech.demomapapp.feature.map.location.ui.LocationControls
import ru.tech.demomapapp.feature.map.mapscreen.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer
import ru.tech.demomapapp.feature.map.ruler.ui.RulerOverlay
import ru.tech.demomapapp.feature.map.tools.ui.ToolsOverlay
import ru.tech.demomapapp.feature.map.viewport.ui.ViewportControls

@Composable
fun MapScreenContent(component: MapScreenUiContract, modifier: Modifier = Modifier) {
    // Cross-feature state from parent
    val parentModel by component.model.subscribeAsState()

    // Child-private states via narrow interfaces
    val locationModel by component.locationUi.model.subscribeAsState()
    val drawingModel by component.drawingUi.model.subscribeAsState()
    val rulerModel by component.rulerUi.model.subscribeAsState()
    val toolsModel by component.toolsUi.model.subscribeAsState()
    val viewportModel by component.viewportUi.model.subscribeAsState()

    // Compose render model from multiple child state sources
    val renderModel = remember(locationModel, drawingModel, rulerModel, viewportModel, toolsModel) {
        composeRenderModel(
            locationModel = locationModel,
            drawingModel = drawingModel,
            rulerModel = rulerModel,
            viewportModel = viewportModel,
            toolsModel = toolsModel,
        )
    }

    // Derived: center marker is disabled during drawing mode
    val isCenterMarkerEnabled = remember(drawingModel) {
        drawingModel.drawingMode == null && !drawingModel.isCreatePointSheetVisible
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        MapRenderer(
            model = renderModel,
            modifier = Modifier.fillMaxSize(),
            viewportCommand = parentModel.pendingViewportCommand,
            onCameraIdle = component::onCameraIdle,
            onViewportCommandConsumed = component.viewportUi::onViewportCommandConsumed,
            onFeatureClick = { click ->
                component.onFeatureClick(
                    points = drawingModel.points,
                    lines = drawingModel.lines,
                    polygons = drawingModel.polygons,
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
            isCenterMarkerMenuVisible = viewportModel.isCenterMarkerMenuVisible,
            isCenterMarkerEnabled = isCenterMarkerEnabled,
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
            isGpsEnabled = locationModel.mode.isGpsActive(),
            isRulerEnabled = parentModel.isRulerEnabled,
            onDismiss = component.toolsUi::onMapToolsDismiss,
            onGpsToggle = component.locationUi::onGpsToggle,
            onRulerToggle = component::onRulerToggle,
        )

        parentModel.selectedFeatureInfoWindow?.let { infoWindow ->
            PointInfoWindowOverlay(
                state = infoWindow,
                onDismiss = component::onFeatureInfoWindowDismiss,
            )
        }
    }
}

/**
 * Composes render model from multiple child state sources.
 * Render data is aggregated here since it spans multiple child features.
 */
private fun composeRenderModel(
    locationModel: LocationModel,
    drawingModel: DrawingModel,
    rulerModel: RulerModel,
    viewportModel: ViewportModel,
    toolsModel: ToolsModel,
) = drawingModel.toRenderModel(
    style = toolsModel.selectedStyle,
    overlayLayers = toolsModel.layers,
    currentSnapshot = viewportModel.cameraSnapshot,
    currentLocationMarker = locationModel.currentMarker,
    rulerMeasurement = rulerModel.measurement,
)

private fun DrawingModel.toRenderModel(
    style: ru.tech.demomapapp.feature.map.api.MapStyle,
    overlayLayers: List<ru.tech.demomapapp.feature.map.api.MapLayerEntry>,
    currentSnapshot: ru.tech.demomapapp.feature.map.api.MapCameraSnapshot?,
    currentLocationMarker: ru.tech.demomapapp.feature.map.api.MapLocationMarker?,
    rulerMeasurement: ru.tech.demomapapp.feature.map.api.RulerMeasurement?,
) = ru.tech.demomapapp.feature.map.api.MapState(
    style = style,
    overlayLayers = overlayLayers,
    points = points,
    lines = lines,
    polygons = polygons,
).toRenderModel(
    shapeDrawingDraft = shapeDrawingDraft?.toComponentDraft(),
    currentSnapshot = currentSnapshot,
    currentLocationMarker = currentLocationMarker,
    rulerMeasurement = rulerMeasurement,
)

private fun ru.tech.demomapapp.feature.map.api.ShapeDrawingDraft.toComponentDraft(): MapScreenComponent.ShapeDrawingDraft =
    MapScreenComponent.ShapeDrawingDraft(
        mode = when (mode) {
            ru.tech.demomapapp.feature.map.api.DrawingMode.LINE -> MapScreenComponent.DrawingMode.LINE
            ru.tech.demomapapp.feature.map.api.DrawingMode.POLYGON -> MapScreenComponent.DrawingMode.POLYGON
        },
        fixedVertices = fixedVertices,
        titleInput = titleInput,
    )

private fun ru.tech.demomapapp.feature.map.api.MyLocationMode.isGpsActive(): Boolean =
    this == ru.tech.demomapapp.feature.map.api.MyLocationMode.GPS