package ru.tech.demomapapp.feature.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapScreen(
    styleUrl: String,
    modifier: Modifier = Modifier,
)
