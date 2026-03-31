package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import ru.tech.demomapapp.feature.map.api.MapLayerEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MapsOnScreenBottomSheet(
    baseMapTitle: String,
    layers: List<MapLayerEntry>,
    onLayerActionsClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleLayers = layers.asReversed()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Настройка слоев загруженных карт",
                style = MaterialTheme.typography.titleMedium,
            )
            visibleLayers.forEach { layer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLayerActionsClick(layer.id) }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = layer.title, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${(layer.opacity * 100).roundToInt()}% непрозрачн.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "Действия",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                HorizontalDivider()
            }
            Text(
                text = "Базовый слой: $baseMapTitle",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun LayerActionsDialog(
    layer: MapLayerEntry,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onChangeOpacity: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(layer.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = onMoveUp, enabled = canMoveUp) {
                    Text("Переместить вверх")
                }
                TextButton(onClick = onMoveDown, enabled = canMoveDown) {
                    Text("Переместить вниз")
                }
                TextButton(onClick = onChangeOpacity) {
                    Text("Изменить прозрачность")
                }
                TextButton(onClick = onRemove) {
                    Text("Убрать слой")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LayerOpacityBottomSheet(
    layerTitle: String,
    opacity: Float,
    onOpacityChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = layerTitle, style = MaterialTheme.typography.titleMedium)
            Text(text = "Прозрачность: ${(opacity * 100).roundToInt()}%")
            Slider(
                value = opacity,
                onValueChange = onOpacityChange,
                valueRange = 0f..1f,
            )
        }
    }
}
