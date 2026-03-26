package ru.tech.demomapapp.feature.map.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot

@Composable
expect fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier = Modifier,
    onCameraIdle: (MapCameraSnapshot) -> Unit = {},
)
