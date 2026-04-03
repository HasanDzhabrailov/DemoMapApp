package ru.tech.demomapapp.feature.map.tools.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun MapToolsMenuOverlay(
    isGpsEnabled: Boolean,
    isRulerEnabled: Boolean,
    onDismiss: () -> Unit,
    onAvailableMapsClick: () -> Unit,
    onMapsOnScreenClick: () -> Unit,
    onGpsToggle: () -> Unit,
    onRulerToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val overlayInteractionSource = remember { MutableInteractionSource() }
    val menuInteractionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.08f))
                .clickable(
                    interactionSource = overlayInteractionSource,
                    indication = null,
                    onClick = onDismiss,
                ),
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 72.dp)
                .width(170.dp)
                .clickable(
                    interactionSource = menuInteractionSource,
                    indication = null,
                    onClick = {},
                ),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xDE181818),
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(vertical = 4.dp),
            ) {
                MapToolsMenuAction(
                    text = "Доступные карты",
                    onClick = onAvailableMapsClick,
                )
                MapToolsMenuAction(
                    text = "Карты на экране",
                    onClick = onMapsOnScreenClick,
                )
                MapToolsMenuToggle(
                    text = "GPS",
                    checked = isGpsEnabled,
                    onToggle = onGpsToggle,
                )
                MapToolsMenuToggle(
                    text = "Рулетка",
                    checked = isRulerEnabled,
                    onToggle = onRulerToggle,
                )
            }
        }
    }
}

@Composable
private fun MapToolsMenuAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 42.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    )
    HorizontalDivider(
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.18f),
        modifier = Modifier.padding(horizontal = 10.dp),
    )
}

@Composable
private fun MapToolsMenuToggle(text: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 42.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
        )
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.18f),
        modifier = Modifier.padding(horizontal = 10.dp),
    )
}
