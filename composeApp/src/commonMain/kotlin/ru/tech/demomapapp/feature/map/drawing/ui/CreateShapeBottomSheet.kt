package ru.tech.demomapapp.feature.map.drawing.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateShapeBottomSheet(
    draft: MapScreenComponent.ShapeDrawingDraft,
    onTitleChange: (String) -> Unit,
    onConfirm: () -> Unit,
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
            OutlinedTextField(
                value = draft.titleInput,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        if (draft.mode == MapScreenComponent.DrawingMode.LINE) {
                            "Название линии"
                        } else {
                            "Название полигона"
                        },
                    )
                },
                singleLine = true,
            )

            Text(
                text = draft.titleInput.ifBlank {
                    if (draft.mode == MapScreenComponent.DrawingMode.LINE) {
                        "Название линии"
                    } else {
                        "Название полигона"
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Создать")
            }
        }
    }
}
