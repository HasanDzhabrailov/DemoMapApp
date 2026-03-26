package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapPoint

internal fun interface MapPointSelectionResolver {
    fun resolve(points: List<MapPoint>, pointKey: String): MapPoint?
}

internal class DefaultMapPointSelectionResolver : MapPointSelectionResolver {
    override fun resolve(points: List<MapPoint>, pointKey: String): MapPoint? =
        points.firstOrNull { point -> point.id == pointKey }
}
