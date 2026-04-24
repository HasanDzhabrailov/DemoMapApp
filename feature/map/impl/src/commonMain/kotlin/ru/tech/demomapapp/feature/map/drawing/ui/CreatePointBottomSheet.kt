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
import ru.tech.demomapapp.feature.map.api.CreatePointDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatePointBottomSheet(
    draft: CreatePointDraft,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
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
                value = draft.latitudeInput,
                onValueChange = onLatitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Широта") },
                singleLine = true,
            )

            OutlinedTextField(
                value = draft.longitudeInput,
                onValueChange = onLongitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Долгота") },
                singleLine = true,
            )

            OutlinedTextField(
                value = draft.titleInput,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название точки") },
                singleLine = true,
            )

            Text(
                text = draft.titleInput.ifBlank { "Название точки" },
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
