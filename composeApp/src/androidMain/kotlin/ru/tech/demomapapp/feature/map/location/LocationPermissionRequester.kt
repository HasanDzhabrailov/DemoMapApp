package ru.tech.demomapapp.feature.map.location

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class LocationPermissionRequester {
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