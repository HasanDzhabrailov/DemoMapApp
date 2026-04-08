package ru.tech.demomapapp.feature.map.viewport.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun MapZoomControlsOverlay(
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 84.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MapZoomButton(
            label = "+",
            onClick = onZoomInClick,
        )
        MapZoomButton(
            label = "-",
            onClick = onZoomOutClick,
        )
    }
}

@Composable
private fun MapZoomButton(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(44.dp),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xD9151515),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
