@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapRenderer(
    modifier: Modifier = Modifier,
    onMapReady: suspend (MapViewHolder) -> Unit = {},
    onStyleLoaded: suspend (MapViewHolder) -> Unit = {},
)
