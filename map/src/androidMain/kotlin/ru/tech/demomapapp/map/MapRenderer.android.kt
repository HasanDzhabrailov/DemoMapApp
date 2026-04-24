@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import kotlinx.coroutines.flow.collectLatest

@Composable
actual fun MapRenderer(
    modifier: Modifier,
    onMapReady: suspend (MapViewHolder) -> Unit,
    onStyleLoaded: suspend (MapViewHolder) -> Unit,
) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current
    if (isInPreview) {
        PreviewMapRenderer(modifier = modifier)
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val savedStateOwner = LocalSavedStateRegistryOwner.current
    val mapViewHolder = rememberMapViewHolder(
        context = context,
        savedStateOwner = savedStateOwner,
    )
    val currentOnMapReady by rememberUpdatedState(onMapReady)
    val currentOnStyleLoaded by rememberUpdatedState(onStyleLoaded)

    BindMapViewLifecycle(
        holder = mapViewHolder,
        lifecycleOwner = lifecycleOwner,
    )

    LaunchedEffect(mapViewHolder) {
        currentOnMapReady(mapViewHolder)
    }

    LaunchedEffect(mapViewHolder) {
        mapViewHolder.styleLoadedEvents.collectLatest {
            currentOnStyleLoaded(mapViewHolder)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapViewHolder.mapView },
    )
}

@Composable
private fun PreviewMapRenderer(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Map preview is unavailable in inspection mode.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
