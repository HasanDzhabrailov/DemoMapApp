@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.feature.map.location.ui

import androidx.compose.runtime.Composable
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapLocationRequest

@Composable
actual fun MapLocationEffectBinder(
    request: MapLocationRequest?,
    onRequestConsumed: () -> Unit,
    onLocationResult: (LocationRequestResult) -> Unit,
) = Unit
