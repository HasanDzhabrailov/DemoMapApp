package ru.tech.demomapapp.feature.map.tools.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.MapCatalogItem
import ru.tech.demomapapp.feature.map.api.MapCatalogItemKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AvailableMapsBottomSheet(
    items: List<MapCatalogItem>,
    onSelect: (String) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Доступные карты",
                style = MaterialTheme.typography.titleMedium,
            )
            items.forEachIndexed { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(item.id) }
                        .padding(vertical = 12.dp),
                ) {
                    Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = if (item.kind == MapCatalogItemKind.BASE_MAP) {
                            "Базовая карта"
                        } else {
                            "Слой поверх карты"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (index != items.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
internal fun ConfirmAddMapDialog(mapTitle: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(mapTitle) },
        text = { Text("Добавить выбранный слой на карту?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}
