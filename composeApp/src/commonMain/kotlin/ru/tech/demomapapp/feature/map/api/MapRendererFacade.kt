package ru.tech.demomapapp.feature.map.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class MapRenderModel(
    val styleUrl: String,
)

@Composable
expect fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier = Modifier,
)
