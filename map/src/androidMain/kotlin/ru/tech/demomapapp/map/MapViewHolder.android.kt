package ru.tech.demomapapp.map

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
import kotlinx.coroutines.flow.MutableSharedFlow
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val MAP_VIEW_STATE_KEY = "map_renderer_view_state"
private const val MAP_COMPASS_MARGIN_HORIZONTAL_PX = 32
private const val MAP_COMPASS_MARGIN_BOTTOM_PX = 88

actual class MapViewHolder internal constructor(
    val mapView: MapView,
) {
    private val styleLoadCoordinator = StyleLoadCoordinator()
    private var mapLibreMap: MapLibreMap? = null
    private var pendingMap: CompletableDeferred<MapLibreMap>? = null
    private var pendingStyle: CompletableDeferred<Style>? = null
    private var areUiSettingsConfigured: Boolean = false
    var isDestroyed: Boolean = false
        private set

    internal val styleLoadedEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

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
        pendingStyle?.cancel(CancellationException("MapViewHolder destroyed before style was ready"))
        pendingMap = null
        pendingStyle = null
        styleLoadCoordinator.reset()
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

    suspend fun loadStyle(styleUrl: String): Style {
        if (isDestroyed) {
            throw CancellationException("MapViewHolder destroyed before style load")
        }

        val map = awaitMap()
        val currentPendingStyle = pendingStyle
        val currentStyle = map.style
        return when (
            val decision = styleLoadCoordinator.onLoadRequested(
                styleUrl = styleUrl,
                hasCurrentStyle = currentStyle != null,
            )
        ) {
            LoadRequestDecision.AwaitPending -> currentPendingStyle!!.await()
            is LoadRequestDecision.UseCurrent -> {
                if (decision.cancelPending) {
                    currentPendingStyle?.cancel(CancellationException("Style load superseded by a newer request"))
                    if (pendingStyle === currentPendingStyle) {
                        pendingStyle = null
                    }
                }
                currentStyle!!
            }

            is LoadRequestDecision.StartNew -> {
                if (decision.cancelPending) {
                    currentPendingStyle?.cancel(CancellationException("Style load superseded by a newer request"))
                    if (pendingStyle === currentPendingStyle) {
                        pendingStyle = null
                    }
                }

                val deferred = CompletableDeferred<Style>()
                pendingStyle = deferred

                map.setStyle(styleUrl) { loadedStyle ->
                    if (isDestroyed) {
                        deferred.cancel(CancellationException("MapViewHolder destroyed before style callback"))
                        if (pendingStyle === deferred) {
                            pendingStyle = null
                        }
                        styleLoadCoordinator.onLoadCancelled(styleUrl)
                        return@setStyle
                    }

                    if (!styleLoadCoordinator.shouldAcceptLoadedStyle(styleUrl) || pendingStyle !== deferred) {
                        deferred.cancel(CancellationException("Style load superseded before callback completed"))
                        styleLoadCoordinator.onLoadCancelled(styleUrl)
                        return@setStyle
                    }

                    if (pendingStyle === deferred) {
                        pendingStyle = null
                    }
                    styleLoadCoordinator.onLoadCompleted(styleUrl)
                    deferred.complete(loadedStyle)
                    styleLoadedEvents.tryEmit(Unit)
                }

                try {
                    deferred.await()
                } catch (error: Throwable) {
                    if (pendingStyle === deferred) {
                        pendingStyle = null
                    }
                    styleLoadCoordinator.onLoadCancelled(styleUrl)
                    throw error
                }
            }
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
