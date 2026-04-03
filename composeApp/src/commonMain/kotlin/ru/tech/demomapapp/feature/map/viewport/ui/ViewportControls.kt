package ru.tech.demomapapp.feature.map.viewport.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun BoxScope.ViewportControls(
    isCenterMarkerMenuVisible: Boolean,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    onCenterMarkerMenuDismiss: () -> Unit,
    onCenterMarkerClick: () -> Unit,
    onCreatePointClick: () -> Unit,
    onCreateLineClick: () -> Unit,
    onCreatePolygonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MapZoomControlsOverlay(
        onZoomInClick = onZoomInClick,
        onZoomOutClick = onZoomOutClick,
        modifier = modifier.align(Alignment.BottomEnd),
    )

    CenterMarker(
        onClick = onCenterMarkerClick,
        modifier = modifier.align(Alignment.Center),
    )

    if (isCenterMarkerMenuVisible) {
        CenterMarkerMenuOverlay(
            onDismiss = onCenterMarkerMenuDismiss,
            onCreatePointClick = onCreatePointClick,
            onCreateLineClick = onCreateLineClick,
            onCreatePolygonClick = onCreatePolygonClick,
        )
    }
}
