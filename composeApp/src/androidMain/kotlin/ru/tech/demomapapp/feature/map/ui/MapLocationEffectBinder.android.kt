package ru.tech.demomapapp.feature.map.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import kotlin.coroutines.resume

private const val LOCATION_REQUEST_TIMEOUT_MILLIS = 5_000L
private const val FRESH_LOCATION_MAX_AGE_MILLIS = 30_000L

@Composable
actual fun MapLocationEffectBinder(
    request: MapLocationRequest?,
    onRequestConsumed: () -> Unit,
    onLocationResult: (LocationRequestResult) -> Unit,
) {
    val context = LocalContext.current
    val permissionRequester = rememberLocationPermissionRequester()
    val locationResolver = remember(context) {
        AndroidCurrentLocationResolver(context.applicationContext)
    }

    LaunchedEffect(request, locationResolver, permissionRequester) {
        request ?: return@LaunchedEffect

        val hasFinePermission = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarsePermission = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionGranted = if (hasFinePermission || hasCoarsePermission) {
            true
        } else {
            permissionRequester.requestLocationPermission()
        }
        if (!permissionGranted) {
            onLocationResult(LocationRequestResult.PermissionDenied)
            onRequestConsumed()
            return@LaunchedEffect
        }

        val resolvedLocation = locationResolver.resolveCurrentLocation(
            hasFinePermission = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION),
            hasCoarsePermission = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION),
        )
        onLocationResult(
            if (resolvedLocation == null) {
                LocationRequestResult.LocationUnavailable
            } else {
                LocationRequestResult.LocationResolved(
                    latitude = resolvedLocation.latitude,
                    longitude = resolvedLocation.longitude,
                )
            },
        )
        onRequestConsumed()
    }
}

@Composable
private fun rememberLocationPermissionRequester(): LocationPermissionRequester {
    val requester = remember { LocationPermissionRequester() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = requester::onPermissionResult,
    )
    requester.registerLauncher {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }
    return requester
}

private class LocationPermissionRequester {
    private var launchRequest: (() -> Unit)? = null
    private var continuation: (Boolean) -> Unit = {}

    fun registerLauncher(launcher: () -> Unit) {
        launchRequest = launcher
    }

    suspend fun requestLocationPermission(): Boolean =
        suspendCancellableCoroutine { continuation ->
            this.continuation = { isGranted ->
                if (continuation.isActive) {
                    continuation.resume(isGranted)
                }
            }
            launchRequest?.invoke() ?: continuation.resume(false)
        }

    fun onPermissionResult(result: Map<String, Boolean>) {
        val isGranted = result.values.any { it }
        continuation(isGranted)
        continuation = {}
    }
}

private class AndroidCurrentLocationResolver(
    private val context: Context,
) {
    private val locationManager: LocationManager? by lazy {
        context.getSystemService(LocationManager::class.java)
    }

    suspend fun resolveCurrentLocation(
        hasFinePermission: Boolean,
        hasCoarsePermission: Boolean,
    ): LocationCoordinate? {
        val manager = locationManager ?: return null
        val providers = manager.candidateProviders(
            hasFinePermission = hasFinePermission,
            hasCoarsePermission = hasCoarsePermission,
        )
        if (providers.isEmpty()) {
            return null
        }

        manager.bestLastKnownLocation(providers)
            ?.takeIf(Location::isFreshEnough)
            ?.let(Location::toCoordinate)
            ?.also { return it }

        providers.forEach { provider ->
            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                manager.awaitCurrentLocationApi30(provider, context)
            } else {
                manager.awaitSingleUpdate(provider)
            }
            if (location != null) {
                return location.toCoordinate()
            }
        }

        return manager.bestLastKnownLocation(providers)?.toCoordinate()
    }
}

private fun Context.hasPermission(permission: String): Boolean =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

private fun LocationManager.candidateProviders(
    hasFinePermission: Boolean,
    hasCoarsePermission: Boolean,
): List<String> = buildList {
    if (hasFinePermission && isProviderEnabledSafe(LocationManager.GPS_PROVIDER)) {
        add(LocationManager.GPS_PROVIDER)
    }
    if ((hasFinePermission || hasCoarsePermission) && isProviderEnabledSafe(LocationManager.NETWORK_PROVIDER)) {
        add(LocationManager.NETWORK_PROVIDER)
    }
    if ((hasFinePermission || hasCoarsePermission) && isProviderEnabledSafe(LocationManager.PASSIVE_PROVIDER)) {
        add(LocationManager.PASSIVE_PROVIDER)
    }
}

private fun LocationManager.isProviderEnabledSafe(provider: String): Boolean =
    runCatching { isProviderEnabled(provider) }.getOrDefault(false)

private suspend fun LocationManager.awaitCurrentLocationApi30(
    provider: String,
    context: Context,
): Location? =
    suspendCancellableCoroutine { continuation ->
        val cancellationSignal = CancellationSignal()
        continuation.invokeOnCancellation { cancellationSignal.cancel() }
        try {
            getCurrentLocation(
                provider,
                cancellationSignal,
                context.mainExecutor,
            ) { location ->
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
        } catch (_: SecurityException) {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }

private suspend fun LocationManager.awaitSingleUpdate(provider: String): Location? =
    withTimeoutOrNull(LOCATION_REQUEST_TIMEOUT_MILLIS) {
        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    removeUpdates(this)
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                }
            }
            continuation.invokeOnCancellation {
                removeUpdates(listener)
            }
            try {
                requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            } catch (_: SecurityException) {
                removeUpdates(listener)
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }

private fun LocationManager.bestLastKnownLocation(providers: List<String>): Location? =
    providers.mapNotNull { provider ->
        runCatching { getLastKnownLocation(provider) }.getOrNull()
    }.maxByOrNull(Location::getTime)

private fun Location.isFreshEnough(): Boolean =
    System.currentTimeMillis() - time <= FRESH_LOCATION_MAX_AGE_MILLIS

private data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double,
)

private fun Location.toCoordinate(): LocationCoordinate =
    LocationCoordinate(
        latitude = latitude,
        longitude = longitude,
    )
