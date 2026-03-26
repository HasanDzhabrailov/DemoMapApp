package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal fun interface MapPointInfoWindowStateMapper {
    fun map(point: MapPoint, anchor: MapScreenComponent.PointInfoWindowAnchor): MapScreenComponent.PointInfoWindow
}

internal class DefaultMapPointInfoWindowStateMapper(
    private val createdAtFormatter: MapPointCreatedAtFormatter = DefaultMapPointCreatedAtFormatter(),
) : MapPointInfoWindowStateMapper {
    override fun map(
        point: MapPoint,
        anchor: MapScreenComponent.PointInfoWindowAnchor,
    ): MapScreenComponent.PointInfoWindow =
        MapScreenComponent.PointInfoWindow(
            title = point.title,
            createdAtText = createdAtFormatter.format(point.createdAtEpochMillis),
            anchor = anchor,
        )
}
