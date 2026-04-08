package ru.tech.demomapapp.feature.map.drawing.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

@Composable
internal fun ShapeDrawingControlsOverlay(
    mode: MapScreenComponent.DrawingMode,
    fixedVertexCount: Int,
    onRemoveLastClick: () -> Unit,
    onAddPositionClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (mode == MapScreenComponent.DrawingMode.LINE) {
                    "Рисование пути"
                } else {
                    "Рисование области"
                },
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onRemoveLastClick,
                    enabled = fixedVertexCount > 0,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Удалить")
                }
                Button(
                    onClick = onAddPositionClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("+ позиция")
                }
                Button(
                    onClick = onDetailsClick,
                    enabled = fixedVertexCount >= minimumVertexCount(mode),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Детали")
                }
            }
            Button(
                onClick = onDismissClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Закрыть")
            }
        }
    }
}

private fun minimumVertexCount(mode: MapScreenComponent.DrawingMode): Int = when (mode) {
    MapScreenComponent.DrawingMode.LINE -> 2
    MapScreenComponent.DrawingMode.POLYGON -> 3
}
