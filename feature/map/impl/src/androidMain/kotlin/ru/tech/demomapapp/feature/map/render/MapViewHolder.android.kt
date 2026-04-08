package ru.tech.demomapapp.feature.map.render

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

private const val MAP_VIEW_STATE_KEY = "map_renderer_view_state"
private const val MAP_COMPASS_MARGIN_HORIZONTAL_PX = 32
private const val MAP_COMPASS_MARGIN_BOTTOM_PX = 88

internal class MapViewHolder(
    val mapView: MapView,
) {
    private var mapLibreMap: MapLibreMap? = null
    private var pendingMap: CompletableDeferred<MapLibreMap>? = null
    private var areUiSettingsConfigured: Boolean = false
    var isDestroyed: Boolean = false
        private set

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
        areUiSettingsConfigured = false
        mapView.onDestroy()
    }

    suspend fun configureUiSettings() {
        if (isDestroyed || areUiSettingsConfigured) {
            return
        }

        awaitMap().configureUiSettings()
        areUiSettingsConfigured = true
    }

    suspend fun applyViewportCommand(command: MapViewportCommand) {
        if (isDestroyed) {
            return
        }

        val map = awaitMap()
        when (command) {
            MapViewportCommand.ZoomIn -> map.animateCamera(CameraUpdateFactory.zoomIn())
            MapViewportCommand.ZoomOut -> map.animateCamera(CameraUpdateFactory.zoomOut())
            is MapViewportCommand.MoveTo -> {
                map.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(command.latitude, command.longitude),
                    ),
                )
            }
        }
    }

    suspend fun awaitMap(): MapLibreMap {
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
internal fun rememberMapViewHolder(context: Context, savedStateOwner: SavedStateRegistryOwner): MapViewHolder {
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

@Composable
internal fun BindMapViewLifecycle(holder: MapViewHolder, lifecycleOwner: LifecycleOwner) {
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

private fun MapLibreMap.configureUiSettings() {
    uiSettings.apply {
        compassGravity = Gravity.BOTTOM or Gravity.END
        setCompassMargins(
            MAP_COMPASS_MARGIN_HORIZONTAL_PX,
            MAP_COMPASS_MARGIN_HORIZONTAL_PX,
            MAP_COMPASS_MARGIN_HORIZONTAL_PX,
            MAP_COMPASS_MARGIN_BOTTOM_PX,
        )
    }
}
