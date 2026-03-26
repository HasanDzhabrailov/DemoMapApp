package ru.tech.demomapapp.feature.map.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.tech.demomapapp.feature.map.api.MapState

@Composable
expect fun MapRenderer(
    state: MapState,
    modifier: Modifier = Modifier,
)
