package ru.tech.demomapapp.feature.map.impl.store.handler

import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.impl.store.MapStore

internal data class LocationResult(
    val state: MapStore.State,
    val viewportCommand: MapViewportCommand? = null,
    val locationRequest: MapLocationRequest? = null,
)

internal class LocationHandler {
    fun handleGpsToggle(state: MapStore.State): LocationResult {
        if (state.activeLocationRequest == MapLocationRequest.EnableGpsLocationRequest) {
            val updatedState = state.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                activeLocationRequest = null,
            )
            return LocationResult(state = updatedState)
        }

        val updatedState = when (state.myLocationMode) {
            MyLocationMode.GPS -> state.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                activeLocationRequest = null,
            )

            MyLocationMode.OFF,
            MyLocationMode.MANUAL_PLACEHOLDER,
            -> state.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                activeLocationRequest = MapLocationRequest.EnableGpsLocationRequest,
            )
        }
        return LocationResult(
            state = updatedState,
            locationRequest = updatedState.activeLocationRequest,
        )
    }

    fun handleMyLocationClick(state: MapStore.State): LocationResult? {
        if (state.myLocationMode == MyLocationMode.GPS) {
            return null
        }

        val snapshot = state.lastCameraSnapshot ?: return null
        val updatedState = state.copy(
            myLocationMode = MyLocationMode.MANUAL_PLACEHOLDER,
            currentLocationMarker = snapshot.toPlaceholderLocationMarker(),
            activeLocationRequest = null,
        )
        return LocationResult(state = updatedState)
    }

    fun handleCurrentLocationFocusClick(state: MapStore.State): LocationResult? {
        return when {
            state.currentLocationMarker != null -> {
                LocationResult(
                    state = state,
                    viewportCommand = MapViewportCommand.MoveTo(
                        latitude = state.currentLocationMarker.latitude,
                        longitude = state.currentLocationMarker.longitude,
                    ),
                )
            }

            state.myLocationMode == MyLocationMode.GPS -> {
                val updatedState = state.copy(
                    activeLocationRequest = MapLocationRequest.RecenterToGpsLocationRequest,
                )
                LocationResult(
                    state = updatedState,
                    locationRequest = MapLocationRequest.RecenterToGpsLocationRequest,
                )
            }

            else -> null
        }
    }

    fun handleLocationResult(
        state: MapStore.State,
        result: LocationRequestResult,
    ): LocationResult {
        val request = state.activeLocationRequest
        val updatedState = when (result) {
            LocationRequestResult.PermissionDenied -> state.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                activeLocationRequest = null,
            )

            LocationRequestResult.LocationUnavailable -> {
                if (state.myLocationMode == MyLocationMode.GPS && request != MapLocationRequest.EnableGpsLocationRequest) {
                    state.copy(activeLocationRequest = null)
                } else {
                    state.copy(
                        myLocationMode = MyLocationMode.OFF,
                        currentLocationMarker = null,
                        activeLocationRequest = null,
                    )
                }
            }

            is LocationRequestResult.LocationResolved -> state.copy(
                myLocationMode = MyLocationMode.GPS,
                currentLocationMarker = MapLocationMarker(
                    latitude = result.latitude,
                    longitude = result.longitude,
                    isPlaceholder = false,
                ),
                activeLocationRequest = null,
            )
        }

        val viewportCommand = if (result is LocationRequestResult.LocationResolved) {
            MapViewportCommand.MoveTo(
                latitude = result.latitude,
                longitude = result.longitude,
            )
        } else {
            null
        }

        return LocationResult(
            state = updatedState,
            viewportCommand = viewportCommand,
        )
    }

    private fun MapCameraSnapshot.toPlaceholderLocationMarker(): MapLocationMarker =
        MapLocationMarker(
            latitude = latitude,
            longitude = longitude,
            isPlaceholder = true,
        )
}