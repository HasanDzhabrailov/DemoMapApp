package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import demomapapp.feature.map.impl.generated.resources.Res
import demomapapp.feature.map.impl.generated.resources.ic_map_manual_location
import demomapapp.feature.map.impl.generated.resources.ic_map_recenter
import demomapapp.feature.map.impl.generated.resources.ic_map_settings
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun MapLeftControlsOverlay(
    onMyLocationClick: () -> Unit,
    isMyLocationEnabled: Boolean,
    onCurrentLocationFocusClick: () -> Unit,
    isCurrentLocationFocusEnabled: Boolean,
    onMapToolsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MapToolsButton(onClick = onMapToolsClick)
        MyLocationButton(
            onClick = onMyLocationClick,
            isEnabled = isMyLocationEnabled,
        )
        CurrentLocationFocusButton(
            onClick = onCurrentLocationFocusClick,
            isEnabled = isCurrentLocationFocusEnabled,
        )
    }
}

@Composable
internal fun MapToolsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(42.dp),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xD9151515),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_map_settings),
                contentDescription = "Настройки карты",
                tint = Color.White,
                modifier = Modifier.size(19.dp),
            )
        }
    }
}

@Composable
internal fun MyLocationButton(onClick: () -> Unit, isEnabled: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(42.dp),
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(10.dp),
        color = if (isEnabled) Color(0xD9151515) else Color(0x8A151515),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_map_manual_location),
                contentDescription = "Мое местоположение",
                tint = if (isEnabled) Color.White else Color.White.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
internal fun CurrentLocationFocusButton(onClick: () -> Unit, isEnabled: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(42.dp),
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(10.dp),
        color = if (isEnabled) Color(0xD9151515) else Color(0x8A151515),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_map_recenter),
                contentDescription = "Перейти к текущей геолокации",
                tint = if (isEnabled) Color.White else Color.White.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
