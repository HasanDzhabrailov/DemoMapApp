package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun CenterMarkerMenuOverlay(
    onDismiss: () -> Unit,
    onCreatePointClick: () -> Unit,
    onCreateLineClick: () -> Unit,
    onCreatePolygonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val overlayInteractionSource = remember { MutableInteractionSource() }
    val menuInteractionSource = remember { MutableInteractionSource() }
    val popupOffsetY = with(LocalDensity.current) { 52.dp.roundToPx() }

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

        Popup(
            alignment = Alignment.Center,
            offset = IntOffset(x = 0, y = popupOffsetY),
            properties = PopupProperties(focusable = false),
        ) {
            Surface(
                modifier = Modifier.clickable(
                    interactionSource = menuInteractionSource,
                    indication = null,
                    onClick = {},
                ),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 8.dp,
            ) {
                androidx.compose.foundation.layout.Column {
                    MenuAction(text = "Создать точку", onClick = onCreatePointClick)
                    MenuAction(text = "Создать линию", onClick = onCreateLineClick)
                    MenuAction(text = "Создать полигон", onClick = onCreatePolygonClick)
                }
            }
        }
    }
}

@Composable
private fun MenuAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        style = MaterialTheme.typography.titleSmall,
    )
}
