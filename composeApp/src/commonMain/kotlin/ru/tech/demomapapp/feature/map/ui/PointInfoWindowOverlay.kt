package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

@Composable
internal fun PointInfoWindowOverlay(
    state: MapScreenComponent.FeatureInfoWindow,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val overlayInteractionSource = remember { MutableInteractionSource() }
    val windowInteractionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = overlayInteractionSource,
                    indication = null,
                    onClick = onDismiss,
                ),
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset { state.anchor.toPopupOffset(density) }
                .clickable(
                    interactionSource = windowInteractionSource,
                    indication = null,
                    onClick = onDismiss,
                ),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = state.createdAtText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun MapScreenComponent.FeatureInfoWindowAnchor.toPopupOffset(
    density: androidx.compose.ui.unit.Density,
): IntOffset = with(density) {
    val horizontalMarginPx = 12.dp.roundToPx()
    val verticalOffsetPx = 88.dp.roundToPx()

    IntOffset(
        x = (screenX + horizontalMarginPx).coerceAtLeast(horizontalMarginPx),
        y = (screenY - verticalOffsetPx).coerceAtLeast(horizontalMarginPx),
    )
}
