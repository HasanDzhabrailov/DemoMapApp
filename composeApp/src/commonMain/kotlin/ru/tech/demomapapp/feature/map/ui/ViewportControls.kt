package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.impl.viewport.ViewportComponent

@Composable
internal fun BoxScope.ViewportControls(
    component: ViewportComponent,
    onCenterMarkerClick: () -> Unit,
    onCreatePointClick: () -> Unit,
    onCreateLineClick: () -> Unit,
    onCreatePolygonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    MapZoomControlsOverlay(
        onZoomInClick = component::onZoomInClick,
        onZoomOutClick = component::onZoomOutClick,
        modifier = modifier.align(Alignment.BottomEnd),
    )

    CenterMarker(
        onClick = onCenterMarkerClick,
        modifier = modifier.align(Alignment.Center),
    )

    if (model.isCenterMarkerMenuVisible) {
        CenterMarkerMenuOverlay(
            onDismiss = component::onCenterMarkerMenuDismiss,
            onCreatePointClick = onCreatePointClick,
            onCreateLineClick = onCreateLineClick,
            onCreatePolygonClick = onCreatePolygonClick,
        )
    }
}
