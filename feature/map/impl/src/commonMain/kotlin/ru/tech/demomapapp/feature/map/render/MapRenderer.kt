@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.feature.map.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

@Composable
expect fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier = Modifier,
    viewportCommand: MapViewportCommand? = null,
    onCameraIdle: (MapCameraSnapshot) -> Unit = {},
    onViewportCommandConsumed: () -> Unit = {},
    onFeatureClick: (RenderFeatureClick) -> Unit = {},
)
