package ru.tech.demomapapp.feature.map.location

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

private const val LOCATION_REQUEST_TIMEOUT_MILLIS = 5_000L
private const val FRESH_LOCATION_MAX_AGE_MILLIS = 30_000L

internal class AndroidLocationResolver(
    private val context: Context,
) {
    private val locationManager: LocationManager? by lazy {
        context.getSystemService(LocationManager::class.java)
    }

    suspend fun resolveCurrentLocation(hasFinePermission: Boolean, hasCoarsePermission: Boolean): LocationCoordinate? {
        val manager = locationManager ?: return null
        val providers = manager.candidateProviders(
            hasFinePermission = hasFinePermission,
            hasCoarsePermission = hasCoarsePermission,
        )
        if (providers.isEmpty()) {
            return null
        }

        manager.bestLastKnownLocation(providers)
            ?.takeIf { it.isFreshEnough() }
            ?.let { return it.toCoordinate() }

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

@SuppressLint("MissingPermission")
@TargetApi(Build.VERSION_CODES.R)
private suspend fun LocationManager.awaitCurrentLocationApi30(provider: String, context: Context): Location? =
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

@SuppressLint("MissingPermission")
private suspend fun LocationManager.awaitSingleUpdate(provider: String): Location? =
    withTimeoutOrNull(LOCATION_REQUEST_TIMEOUT_MILLIS) {
        suspendCancellableCoroutine { continuation ->
            val listener = object : android.location.LocationListener {
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

@SuppressLint("MissingPermission")
private fun LocationManager.bestLastKnownLocation(providers: List<String>): Location? =
    providers.mapNotNull { provider ->
        runCatching { getLastKnownLocation(provider) }.getOrNull()
    }.maxByOrNull { it.time }

private fun Location.isFreshEnough(): Boolean = System.currentTimeMillis() - time <= FRESH_LOCATION_MAX_AGE_MILLIS

internal data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double,
)

private fun Location.toCoordinate(): LocationCoordinate = LocationCoordinate(
    latitude = latitude,
    longitude = longitude,
)
