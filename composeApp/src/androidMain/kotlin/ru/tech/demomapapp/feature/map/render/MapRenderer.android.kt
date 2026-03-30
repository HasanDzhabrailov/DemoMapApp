package ru.tech.demomapapp.feature.map.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

@Composable
actual fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier,
    viewportCommand: MapViewportCommand?,
    onCameraIdle: (MapCameraSnapshot) -> Unit,
    onViewportCommandConsumed: () -> Unit,
    onFeatureClick: (RenderFeatureClick) -> Unit,
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

    BindMapViewLifecycle(
        holder = mapViewHolder,
        lifecycleOwner = lifecycleOwner,
    )

    ApplyMapRenderModel(
        holder = mapViewHolder,
        model = model,
    )

    ConfigureMapUiSettings(holder = mapViewHolder)

    BindCameraObservation(
        holder = mapViewHolder,
        onCameraIdle = onCameraIdle,
    )

    BindFeatureClickObservation(
        holder = mapViewHolder,
        onFeatureClick = onFeatureClick,
    )

    ApplyViewportCommand(
        holder = mapViewHolder,
        viewportCommand = viewportCommand,
        onViewportCommandConsumed = onViewportCommandConsumed,
    )

    AndroidView(
        modifier = modifier,
        factory = { mapViewHolder.mapView },
    )
}

@Composable
private fun ApplyViewportCommand(
    holder: MapViewHolder,
    viewportCommand: MapViewportCommand?,
    onViewportCommandConsumed: () -> Unit,
) {
    LaunchedEffect(holder, viewportCommand) {
        val command = viewportCommand ?: return@LaunchedEffect
        holder.applyViewportCommand(command)
        onViewportCommandConsumed()
    }
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

@Composable
private fun ApplyMapRenderModel(
    holder: MapViewHolder,
    model: MapRenderModel,
) {
    val modelApplier = remember(holder) {
        MapRenderModelApplier()
    }

    LaunchedEffect(holder, model) {
        modelApplier.apply(holder = holder, model = model)
    }
}

@Composable
private fun ConfigureMapUiSettings(holder: MapViewHolder) {
    LaunchedEffect(holder) {
        holder.configureUiSettings()
    }
}

@Composable
private fun BindCameraObservation(
    holder: MapViewHolder,
    onCameraIdle: (MapCameraSnapshot) -> Unit,
) {
    val cameraAdapter = remember(onCameraIdle) {
        MapLibreCameraAdapter(onCameraIdle = onCameraIdle)
    }

    LaunchedEffect(holder, cameraAdapter) {
        cameraAdapter.attach(holder.awaitMap())
    }

    DisposableEffect(holder, cameraAdapter) {
        onDispose {
            cameraAdapter.detach()
        }
    }
}

@Composable
private fun BindFeatureClickObservation(
    holder: MapViewHolder,
    onFeatureClick: (RenderFeatureClick) -> Unit,
) {
    val pointClickAdapter = remember(onFeatureClick) {
        MapLibreFeatureClickAdapter(onFeatureClick = onFeatureClick)
    }

    LaunchedEffect(holder, pointClickAdapter) {
        pointClickAdapter.attach(holder.awaitMap())
    }

    DisposableEffect(holder, pointClickAdapter) {
        onDispose {
            pointClickAdapter.detach()
        }
    }
}
