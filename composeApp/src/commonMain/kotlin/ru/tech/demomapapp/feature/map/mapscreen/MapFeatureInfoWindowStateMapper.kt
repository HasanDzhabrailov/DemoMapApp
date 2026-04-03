package ru.tech.demomapapp.feature.map.mapscreen

import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.DefaultMapPointCreatedAtFormatter
import ru.tech.demomapapp.feature.map.impl.MapPointCreatedAtFormatter

internal fun interface MapFeatureInfoWindowStateMapper {
    fun map(
        feature: SelectedMapFeature,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ): MapScreenComponent.FeatureInfoWindow
}

internal class DefaultMapFeatureInfoWindowStateMapper(
    private val createdAtFormatter: MapPointCreatedAtFormatter = DefaultMapPointCreatedAtFormatter(),
) : MapFeatureInfoWindowStateMapper {
    override fun map(
        feature: SelectedMapFeature,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ): MapScreenComponent.FeatureInfoWindow = MapScreenComponent.FeatureInfoWindow(
        title = feature.title,
        createdAtText = createdAtFormatter.format(feature.createdAtEpochMillis),
        anchor = anchor,
    )
}
