package ru.tech.demomapapp.feature.map.render

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
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
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import kotlin.coroutines.resume
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textAnchor
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textHaloColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloWidth
import org.maplibre.android.style.layers.PropertyFactory.textOffset
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val MAP_VIEW_STATE_KEY = "map_renderer_view_state"
private const val MAP_COMPASS_MARGIN_PX = 32
private const val MAP_POINTS_SOURCE_ID = "map-renderer-points-source"
internal const val MAP_POINTS_LAYER_ID = "map-renderer-points-layer"
private const val MAP_POINT_LABELS_LAYER_ID = "map-renderer-point-labels-layer"
private const val MAP_POINT_LABEL_PROPERTY = "label"
internal const val MAP_POINT_KEY_PROPERTY = "pointKey"

@Composable
actual fun MapRenderer(
    model: MapRenderModel,
    modifier: Modifier,
    onCameraIdle: (MapCameraSnapshot) -> Unit,
    onPointClick: (RenderPointClick) -> Unit,
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

    BindPointClickObservation(
        holder = mapViewHolder,
        onPointClick = onPointClick,
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
    LaunchedEffect(holder, model) {
        holder.applyRenderModel(model)
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
private fun BindPointClickObservation(
    holder: MapViewHolder,
    onPointClick: (RenderPointClick) -> Unit,
) {
    val pointClickAdapter = remember(onPointClick) {
        MapLibrePointClickAdapter(onPointClick = onPointClick)
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

internal class MapViewHolder(
    val mapView: MapView,
) {
    private var mapLibreMap: MapLibreMap? = null
    private var pendingMap: CompletableDeferred<MapLibreMap>? = null
    private var lastAppliedStyle: RenderMapStyle? = null
    private var areUiSettingsConfigured: Boolean = false
    var isDestroyed: Boolean = false
        private set

    suspend fun applyRenderModel(model: MapRenderModel) {
        if (isDestroyed) {
            return
        }

        val map = awaitMap()
        val style = map.loadStyle(model.style, lastAppliedStyle)
        lastAppliedStyle = model.style
        style.applyPoints(model.points)
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

private fun MapLibreMap.configureUiSettings() {
    uiSettings.apply {
        compassGravity = Gravity.TOP or Gravity.END
        setCompassMargins(
            MAP_COMPASS_MARGIN_PX,
            MAP_COMPASS_MARGIN_PX,
            MAP_COMPASS_MARGIN_PX,
            MAP_COMPASS_MARGIN_PX,
        )
    }
}

private suspend fun MapLibreMap.loadStyle(
    style: RenderMapStyle,
    lastAppliedStyle: RenderMapStyle?,
): Style {
    val currentStyle = this.style
    if (lastAppliedStyle == style && currentStyle != null) {
        return currentStyle
    }

    return suspendCancellableCoroutine { continuation ->
        setStyle(style.styleUrl()) { loadedStyle ->
            if (continuation.isActive) {
                continuation.resume(loadedStyle)
            }
        }
    }
}

private fun Style.applyPoints(points: List<RenderMapPoint>) {
    val featureCollection = FeatureCollection.fromFeatures(
        points.map { point ->
            Feature.fromGeometry(
                Point.fromLngLat(point.longitude, point.latitude),
            ).apply {
                addStringProperty(MAP_POINT_KEY_PROPERTY, point.key)
                addStringProperty(MAP_POINT_LABEL_PROPERTY, point.label)
            }
        },
    )

    val source = getSourceAs<GeoJsonSource>(MAP_POINTS_SOURCE_ID)
        ?: GeoJsonSource(MAP_POINTS_SOURCE_ID, featureCollection).also(::addSource)
    source.setGeoJson(featureCollection)

    if (getLayer(MAP_POINTS_LAYER_ID) == null) {
        addLayer(
            CircleLayer(MAP_POINTS_LAYER_ID, MAP_POINTS_SOURCE_ID).withProperties(
                circleColor("#D95D39"),
                circleRadius(7f),
                circleOpacity(0.95f),
                circleStrokeColor("#FFF7F0"),
                circleStrokeWidth(2f),
            ),
        )
    }

    if (getLayer(MAP_POINT_LABELS_LAYER_ID) == null) {
        addLayer(
            SymbolLayer(MAP_POINT_LABELS_LAYER_ID, MAP_POINTS_SOURCE_ID).withProperties(
                textField("{$MAP_POINT_LABEL_PROPERTY}"),
                textSize(12f),
                textColor("#2B211D"),
                textHaloColor("#FFF7F0"),
                textHaloWidth(1.5f),
                textOffset(arrayOf(0f, 1.4f)),
                textAnchor("top"),
                textAllowOverlap(false),
            ),
        )
    }
}

private fun RenderMapStyle.styleUrl(): String =
    when (this) {
        RenderMapStyle.DEFAULT -> "https://demotiles.maplibre.org/style.json"
    }
