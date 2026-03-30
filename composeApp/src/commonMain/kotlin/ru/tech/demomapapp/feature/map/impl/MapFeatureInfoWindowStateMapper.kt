package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.impl.store.MapStore

internal fun interface MapFeatureInfoWindowStateMapper {
    fun map(feature: SelectedMapFeature, anchor: MapStore.FeatureInfoWindowAnchor): MapStore.FeatureInfoWindow
}

internal class DefaultMapFeatureInfoWindowStateMapper(
    private val createdAtFormatter: MapPointCreatedAtFormatter = DefaultMapPointCreatedAtFormatter(),
) : MapFeatureInfoWindowStateMapper {
    override fun map(
        feature: SelectedMapFeature,
        anchor: MapStore.FeatureInfoWindowAnchor,
    ): MapStore.FeatureInfoWindow = MapStore.FeatureInfoWindow(
        title = feature.title,
        createdAtText = createdAtFormatter.format(feature.createdAtEpochMillis),
        anchor = anchor,
    )
}
