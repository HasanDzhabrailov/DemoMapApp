package ru.tech.demomapapp.feature.map.ui

import androidx.compose.runtime.Composable
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapLocationRequest

@Composable
expect fun MapLocationEffectBinder(
    request: MapLocationRequest?,
    onRequestConsumed: () -> Unit,
    onLocationResult: (LocationRequestResult) -> Unit,
)
