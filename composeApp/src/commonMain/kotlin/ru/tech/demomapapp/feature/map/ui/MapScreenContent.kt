package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.toRenderModel
import ru.tech.demomapapp.feature.map.render.RenderPointClick
import ru.tech.demomapapp.feature.map.render.MapRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(
    component: MapScreenComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val debugModel by component.debugModel.subscribeAsState()
    val renderModel = model.mapState.toRenderModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        MapRenderer(
            model = renderModel,
            modifier = Modifier.fillMaxSize(),
            onCameraIdle = component::onCameraIdle,
            onPointClick = { click -> component.onPointClick(click.pointKey, click.toPointInfoWindowAnchor()) },
        )

        CenterMarker(
            onClick = component::onCenterMarkerClick,
            modifier = Modifier.align(Alignment.Center),
        )

        MapDebugPanel(
            model = debugModel,
            snapshot = model.lastCameraSnapshot,
            onToggle = component::onDebugPanelToggle,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        )

        if (model.isCenterMarkerMenuVisible) {
            CenterMarkerMenuOverlay(
                onDismiss = component::onCenterMarkerMenuDismiss,
                onCreatePointClick = component::onCreatePointClick,
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

        model.selectedPointInfoWindow?.let { infoWindow ->
            PointInfoWindowOverlay(
                state = infoWindow,
                onDismiss = component::onPointInfoWindowDismiss,
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

    override val debugModel: Value<MapScreenComponent.DebugModel> =
        MutableValue(
            MapScreenComponent.DebugModel(
                isExpanded = true,
            ),
        )

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = Unit

    override fun onCenterMarkerClick() = Unit

    override fun onCenterMarkerMenuDismiss() = Unit

    override fun onCreatePointClick() = Unit

    override fun onCreatePointLatitudeChange(value: String) = Unit

    override fun onCreatePointLongitudeChange(value: String) = Unit

    override fun onCreatePointTitleChange(value: String) = Unit

    override fun onCreatePointConfirm() = Unit

    override fun onCreatePointSheetDismiss() = Unit

    override fun onPointClick(
        pointKey: String,
        anchor: MapScreenComponent.PointInfoWindowAnchor,
    ) = Unit

    override fun onPointInfoWindowDismiss() = Unit

    override fun onDebugPanelToggle() = Unit
}

private fun RenderPointClick.toPointInfoWindowAnchor(): MapScreenComponent.PointInfoWindowAnchor =
    MapScreenComponent.PointInfoWindowAnchor(
        screenX = anchor.screenX,
        screenY = anchor.screenY,
    )
