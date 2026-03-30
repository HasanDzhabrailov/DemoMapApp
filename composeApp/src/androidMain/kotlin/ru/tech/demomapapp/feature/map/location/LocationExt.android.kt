package ru.tech.demomapapp.feature.map.location

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager

internal fun Context.hasPermission(permission: String): Boolean =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

internal fun LocationManager.candidateProviders(
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

internal fun LocationManager.isProviderEnabledSafe(provider: String): Boolean =
    runCatching { isProviderEnabled(provider) }.getOrDefault(false)