package ru.tech.demomapapp.feature.map.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier = Modifier,
)
