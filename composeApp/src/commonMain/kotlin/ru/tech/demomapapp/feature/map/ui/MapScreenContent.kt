package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer
import ru.tech.demomapapp.feature.map.render.RenderFeatureClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(
    component: MapScreenComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val renderModel = model.mapState.toRenderModel(
        shapeDrawingDraft = model.shapeDrawingDraft,
        currentSnapshot = model.lastCameraSnapshot,
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

        MapToolsButton(
            onClick = component::onMapToolsClick,
            modifier = Modifier.align(Alignment.BottomStart),
        )

        MapZoomControlsOverlay(
            onZoomInClick = component::onZoomInClick,
            onZoomOutClick = component::onZoomOutClick,
            modifier = Modifier.align(Alignment.BottomEnd),
        )

        if (model.isMapToolsMenuVisible) {
            MapToolsMenuOverlay(
                isGpsEnabled = model.isGpsEnabled,
                isRulerEnabled = model.isRulerEnabled,
                onDismiss = component::onMapToolsDismiss,
                onAvailableMapsClick = component::onAvailableMapsClick,
                onMapsOnScreenClick = component::onMapsOnScreenClick,
                onGpsToggle = component::onGpsToggle,
                onRulerToggle = component::onRulerToggle,
            )
        }

        CenterMarker(
            onClick = component::onCenterMarkerClick,
            modifier = Modifier.align(Alignment.Center),
        )

        if (model.isCenterMarkerMenuVisible) {
            CenterMarkerMenuOverlay(
                onDismiss = component::onCenterMarkerMenuDismiss,
                onCreatePointClick = component::onCreatePointClick,
                onCreateLineClick = component::onCreateLineClick,
                onCreatePolygonClick = component::onCreatePolygonClick,
            )
        }

        if (model.isCreatePointSheetVisible) {
            model.createPointDraft?.let { draft ->
                CreatePointBottomSheet(
                    draft = draft,
                    onLatitudeChange = component::onCreatePointLatitudeChange,
                    onLongitudeChange = component::onCreatePointLongitudeChange,
                    onTitleChange = component::onCreatePointTitleChange,
                    onConfirm = component::onCreatePointConfirm,
                    onDismiss = component::onCreatePointSheetDismiss,
                )
            }
        }

        model.shapeDrawingDraft?.let { draft ->
            ShapeDrawingControlsOverlay(
                mode = draft.mode,
                fixedVertexCount = draft.fixedVertices.size,
                onRemoveLastClick = component::onDrawingRemoveLastPositionClick,
                onAddPositionClick = component::onDrawingAddPositionClick,
                onDetailsClick = component::onDrawingDetailsClick,
                onDismissClick = component::onDrawingDismiss,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        if (model.isCreateShapeSheetVisible) {
            model.shapeDrawingDraft?.let { draft ->
                CreateShapeBottomSheet(
                    draft = draft,
                    onTitleChange = component::onCreateShapeTitleChange,
                    onConfirm = component::onCreateShapeConfirm,
                    onDismiss = component::onCreateShapeSheetDismiss,
                )
            }
        }

        model.selectedFeatureInfoWindow?.let { infoWindow ->
            PointInfoWindowOverlay(
                state = infoWindow,
                onDismiss = component::onFeatureInfoWindowDismiss,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun MapScreenContentPreview() {
    MaterialTheme {
        MapScreenContent(component = PreviewMapScreenComponent())
    }
}

private class PreviewMapScreenComponent : MapScreenComponent {
    override val model: Value<MapScreenComponent.Model> =
        MutableValue(
            MapScreenComponent.Model(
                lastCameraSnapshot = MapCameraSnapshot(
                    latitude = 55.75124,
                    longitude = 37.61842,
                    zoom = 12.34567,
                    bearing = 18.2,
                ),
            ),
        )

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = Unit

    override fun onMapToolsClick() = Unit

    override fun onMapToolsDismiss() = Unit

    override fun onZoomInClick() = Unit

    override fun onZoomOutClick() = Unit

    override fun onAvailableMapsClick() = Unit

    override fun onMapsOnScreenClick() = Unit

    override fun onGpsToggle() = Unit

    override fun onRulerToggle() = Unit

    override fun onViewportCommandConsumed() = Unit

    override fun onCenterMarkerClick() = Unit

    override fun onCenterMarkerMenuDismiss() = Unit

    override fun onCreatePointClick() = Unit

    override fun onCreateLineClick() = Unit

    override fun onCreatePolygonClick() = Unit

    override fun onCreatePointLatitudeChange(value: String) = Unit

    override fun onCreatePointLongitudeChange(value: String) = Unit

    override fun onCreatePointTitleChange(value: String) = Unit

    override fun onCreatePointConfirm() = Unit

    override fun onCreatePointSheetDismiss() = Unit

    override fun onDrawingAddPositionClick() = Unit

    override fun onDrawingRemoveLastPositionClick() = Unit

    override fun onDrawingDetailsClick() = Unit

    override fun onDrawingDismiss() = Unit

    override fun onCreateShapeTitleChange(value: String) = Unit

    override fun onCreateShapeConfirm() = Unit

    override fun onCreateShapeSheetDismiss() = Unit

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) = Unit

    override fun onFeatureInfoWindowDismiss() = Unit
}

private fun RenderFeatureClick.toFeatureInfoWindowAnchor(): MapScreenComponent.FeatureInfoWindowAnchor =
    MapScreenComponent.FeatureInfoWindowAnchor(
        screenX = anchor.screenX,
        screenY = anchor.screenY,
    )

private fun RenderFeatureClick.toFeatureType(): MapScreenComponent.FeatureType =
    when (featureType) {
        ru.tech.demomapapp.feature.map.render.RenderFeatureType.POINT -> MapScreenComponent.FeatureType.POINT
        ru.tech.demomapapp.feature.map.render.RenderFeatureType.LINE -> MapScreenComponent.FeatureType.LINE
        ru.tech.demomapapp.feature.map.render.RenderFeatureType.POLYGON -> MapScreenComponent.FeatureType.POLYGON
    }
