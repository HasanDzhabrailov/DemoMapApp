@file:Suppress("ktlint:standard:function-naming")

package ru.tech.demomapapp.feature.map.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.location.AndroidLocationResolver
import ru.tech.demomapapp.feature.map.location.LocationPermissionRequester
import ru.tech.demomapapp.feature.map.location.hasPermission

@Composable
actual fun MapLocationEffectBinder(
    request: MapLocationRequest?,
    onRequestConsumed: () -> Unit,
    onLocationResult: (LocationRequestResult) -> Unit,
) {
    val context = LocalContext.current
    val permissionRequester = rememberLocationPermissionRequester()
    val locationResolver = remember(context) {
        AndroidLocationResolver(context.applicationContext)
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
