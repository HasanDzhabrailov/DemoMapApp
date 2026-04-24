@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.feature.map.render

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.map.MapRenderer
import ru.tech.demomapapp.map.MapViewHolder

@Composable
actual fun FeatureMapRenderer(
    model: MapRenderModel,
    modifier: Modifier,
    viewportCommand: MapViewportCommand?,
    onCameraIdle: (MapCameraSnapshot) -> Unit,
    onViewportCommandConsumed: () -> Unit,
    onFeatureClick: (RenderFeatureClick) -> Unit,
) {
    var mapViewHolder by remember { mutableStateOf<MapViewHolder?>(null) }

    MapRenderer(
        modifier = modifier,
        onMapReady = { holder ->
            mapViewHolder = holder
            holder.configureUiSettings()
        },
    )

    ApplyMapRenderModel(
        holder = mapViewHolder,
        model = model,
    )

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
}

@Composable
private fun ApplyViewportCommand(
    holder: MapViewHolder?,
    viewportCommand: MapViewportCommand?,
    onViewportCommandConsumed: () -> Unit,
) {
    LaunchedEffect(holder, viewportCommand) {
        val readyHolder = holder ?: return@LaunchedEffect
        val command = viewportCommand ?: return@LaunchedEffect
        readyHolder.awaitMap().applyViewportCommand(command)
        onViewportCommandConsumed()
    }
}

@Composable
private fun ApplyMapRenderModel(holder: MapViewHolder?, model: MapRenderModel) {
    val modelApplier = remember(holder) {
        MapRenderModelApplier()
    }

    LaunchedEffect(holder, model) {
        val readyHolder = holder ?: return@LaunchedEffect
        modelApplier.apply(holder = readyHolder, model = model)
    }
}

@Composable
private fun BindCameraObservation(holder: MapViewHolder?, onCameraIdle: (MapCameraSnapshot) -> Unit) {
    val cameraAdapter = remember(onCameraIdle) {
        MapLibreCameraAdapter(onCameraIdle = onCameraIdle)
    }

    LaunchedEffect(holder, cameraAdapter) {
        val readyHolder = holder ?: return@LaunchedEffect
        cameraAdapter.attach(readyHolder.awaitMap())
    }

    DisposableEffect(holder, cameraAdapter) {
        onDispose {
            cameraAdapter.detach()
        }
    }
}

@Composable
private fun BindFeatureClickObservation(holder: MapViewHolder?, onFeatureClick: (RenderFeatureClick) -> Unit) {
    val pointClickAdapter = remember(onFeatureClick) {
        MapLibreFeatureClickAdapter(onFeatureClick = onFeatureClick)
    }

    LaunchedEffect(holder, pointClickAdapter) {
        val readyHolder = holder ?: return@LaunchedEffect
        pointClickAdapter.attach(readyHolder.awaitMap())
    }

    DisposableEffect(holder, pointClickAdapter) {
        onDispose {
            pointClickAdapter.detach()
        }
    }
}

private fun org.maplibre.android.maps.MapLibreMap.applyViewportCommand(command: MapViewportCommand) {
    when (command) {
        MapViewportCommand.ZoomIn -> animateCamera(CameraUpdateFactory.zoomIn())
        MapViewportCommand.ZoomOut -> animateCamera(CameraUpdateFactory.zoomOut())
        is MapViewportCommand.MoveTo -> {
            animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(command.latitude, command.longitude),
                ),
            )
        }
    }
}
