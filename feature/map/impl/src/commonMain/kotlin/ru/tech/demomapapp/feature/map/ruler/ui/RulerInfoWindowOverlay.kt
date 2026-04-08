package ru.tech.demomapapp.feature.map.ruler.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState

@Composable
internal fun RulerInfoWindowOverlay(state: RulerInfoWindowState, modifier: Modifier = Modifier) {
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)

    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .drawBehind {
                val pointerWidth = 18.dp.toPx()
                val pointerHeight = 12.dp.toPx()
                val centerX = size.width / 2f
                val bottomY = size.height
                val path = Path().apply {
                    moveTo(centerX - pointerWidth / 2f, bottomY)
                    lineTo(centerX, bottomY + pointerHeight)
                    lineTo(centerX + pointerWidth / 2f, bottomY)
                    close()
                }
                drawPath(
                    path = path,
                    color = surfaceColor,
                )
            },
        shape = MaterialTheme.shapes.large,
        color = surfaceColor,
        tonalElevation = 6.dp,
        shadowElevation = 10.dp,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = state.distanceText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = state.trueAzimuthText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            state.magneticAzimuthText?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            state.directionalAngleText?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
