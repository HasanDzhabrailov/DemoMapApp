package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.round
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

@Composable
internal fun MapDebugPanel(
    model: MapScreenComponent.DebugModel,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Debug Panel",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = model.headerText(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = if (model.isExpanded) "Hide" else "Show",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            if (model.isExpanded) {
                MapDebugPanelBody(
                    snapshot = model.lastCameraSnapshot,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun MapDebugPanelBody(
    snapshot: MapCameraSnapshot?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (snapshot == null) {
            Text(
                text = "Move the map to capture camera values.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return
        }

        MapDebugValueRow(label = "Latitude", value = snapshot.latitude.formatDebug())
        MapDebugValueRow(label = "Longitude", value = snapshot.longitude.formatDebug())
        MapDebugValueRow(label = "Zoom", value = snapshot.zoom.formatDebug())
        MapDebugValueRow(label = "Bearing", value = snapshot.bearing.formatDebug())
    }
}

@Composable
private fun MapDebugValueRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun MapScreenComponent.DebugModel.headerText(): String {
    if (isExpanded) {
        return "Tap to hide"
    }

    val snapshot = lastCameraSnapshot ?: return "Tap to show"
    return "lat ${snapshot.latitude.formatDebug()} lon ${snapshot.longitude.formatDebug()} z ${snapshot.zoom.formatDebug()}"
}

private fun Double.formatDebug(): String {
    val scaled = round(this * 100000.0) / 100000.0
    return scaled.toString()
}
