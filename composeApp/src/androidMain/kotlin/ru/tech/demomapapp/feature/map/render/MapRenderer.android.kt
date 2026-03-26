package ru.tech.demomapapp.feature.map.render

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

private const val MAP_VIEW_STATE_KEY = "map_renderer_view_state"

@Composable
actual fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier,
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

@Composable
private fun BindMapViewLifecycle(
    holder: MapViewHolder,
    lifecycleOwner: LifecycleOwner,
) {
    val mapView = holder.mapView

    DisposableEffect(lifecycleOwner, holder) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> holder.destroy()
                else -> Unit
            }
        }

        lifecycle.addObserver(observer)

        when {
            lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) -> {
                mapView.onStart()
                mapView.onResume()
            }

            lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) -> {
                mapView.onStart()
            }
        }

        onDispose {
            lifecycle.removeObserver(observer)
            if (!holder.isDestroyed) {
                holder.destroy()
            }
        }
    }
}

@Composable
private fun ApplyMapRenderModel(
    holder: MapViewHolder,
    model: MapRenderModel,
) {
    LaunchedEffect(holder, model.style) {
        holder.applyStyle(model.style)
    }
}

private class MapViewHolder(
    val mapView: MapView,
) {
    private var mapLibreMap: MapLibreMap? = null
    private var pendingMap: CompletableDeferred<MapLibreMap>? = null
    private var lastAppliedStyle: RenderMapStyle? = null
    var isDestroyed: Boolean = false
        private set

    suspend fun applyStyle(style: RenderMapStyle) {
        if (isDestroyed) {
            return
        }

        val map = awaitMap()
        if (lastAppliedStyle == style) {
            return
        }

        map.setStyle(style.styleUrl())
        lastAppliedStyle = style
    }

    fun onLowMemory() {
        if (!isDestroyed) {
            mapView.onLowMemory()
        }
    }

    fun saveState(outState: Bundle) {
        if (!isDestroyed) {
            mapView.onSaveInstanceState(outState)
        }
    }

    fun destroy() {
        if (isDestroyed) {
            return
        }

        isDestroyed = true
        pendingMap?.cancel(CancellationException("MapViewHolder destroyed before map was ready"))
        pendingMap = null
        mapLibreMap = null
        lastAppliedStyle = null
        mapView.onDestroy()
    }

    private suspend fun awaitMap(): MapLibreMap {
        mapLibreMap?.let { return it }
        pendingMap?.let { return it.await() }

        val deferred = CompletableDeferred<MapLibreMap>()
        pendingMap = deferred

        mapView.getMapAsync { readyMap ->
            if (isDestroyed) {
                deferred.cancel(CancellationException("MapViewHolder destroyed before map callback"))
                if (pendingMap === deferred) {
                    pendingMap = null
                }
                return@getMapAsync
            }

            mapLibreMap = readyMap
            if (pendingMap === deferred) {
                pendingMap = null
            }
            deferred.complete(readyMap)
        }

        return try {
            deferred.await()
        } catch (error: Throwable) {
            if (pendingMap === deferred) {
                pendingMap = null
            }
            throw error
        }
    }
}

@Composable
private fun rememberMapViewHolder(
    context: Context,
    savedStateOwner: SavedStateRegistryOwner,
): MapViewHolder {
    val appContext = context.applicationContext
    val restoredState = remember(savedStateOwner) {
        savedStateOwner.savedStateRegistry.consumeRestoredStateForKey(MAP_VIEW_STATE_KEY)
    }
    val holder = remember(context, savedStateOwner) {
        MapViewHolder(
            mapView = MapView(context).apply {
                onCreate(restoredState)
            },
        )
    }

    DisposableEffect(holder, appContext, savedStateOwner) {
        val callbacks = object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) = Unit

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onLowMemory() {
                holder.onLowMemory()
            }

            @Suppress("DEPRECATION")
            override fun onTrimMemory(level: Int) {
                if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
                    holder.onLowMemory()
                }
            }
        }
        val savedStateProvider = androidx.savedstate.SavedStateRegistry.SavedStateProvider {
            Bundle().apply(holder::saveState)
        }

        appContext.registerComponentCallbacks(callbacks)
        savedStateOwner.savedStateRegistry.registerSavedStateProvider(
            MAP_VIEW_STATE_KEY,
            savedStateProvider,
        )

        onDispose {
            savedStateOwner.savedStateRegistry.unregisterSavedStateProvider(MAP_VIEW_STATE_KEY)
            appContext.unregisterComponentCallbacks(callbacks)
        }
    }

    return holder
}

private fun RenderMapStyle.styleUrl(): String =
    when (this) {
        RenderMapStyle.DEFAULT -> "https://demotiles.maplibre.org/style.json"
    }
