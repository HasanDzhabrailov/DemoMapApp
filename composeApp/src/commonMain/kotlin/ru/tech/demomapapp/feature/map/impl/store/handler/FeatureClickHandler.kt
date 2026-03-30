package ru.tech.demomapapp.feature.map.impl.store.handler

import ru.tech.demomapapp.feature.map.impl.MapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.MapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreMessage

internal class FeatureClickHandler(
    private val featureSelectionResolver: MapFeatureSelectionResolver,
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper,
) {
    fun handleFeatureClick(
        state: MapStore.State,
        featureKey: String,
        featureType: MapStore.FeatureType,
        anchor: MapStore.FeatureInfoWindowAnchor,
        onInfoWindowOpened: (MapStoreMessage) -> Unit,
    ) {
        val feature = featureSelectionResolver.resolve(
            mapState = state.mapState,
            featureKey = featureKey,
            featureType = featureType,
        ) ?: return

        onInfoWindowOpened(
            MapStoreMessage.FeatureInfoWindowOpened(
                infoWindow = featureInfoWindowStateMapper.map(
                    feature = feature,
                    anchor = anchor,
                ),
            ),
        )
    }
}
