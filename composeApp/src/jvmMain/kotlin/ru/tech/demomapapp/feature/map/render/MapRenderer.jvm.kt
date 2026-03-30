@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.feature.map.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

@Composable
actual fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier,
    viewportCommand: MapViewportCommand?,
    onCameraIdle: (MapCameraSnapshot) -> Unit,
    onViewportCommandConsumed: () -> Unit,
    onFeatureClick: (RenderFeatureClick) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Map rendering is available on Android only.\n${model.style}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
