package ru.tech.demomapapp.feature.map.impl.location

import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MyLocationMode

data class LocationModel(
    val mode: MyLocationMode = MyLocationMode.OFF,
    val currentMarker: MapLocationMarker? = null,
    val pendingRequest: MapLocationRequest? = null,
)
