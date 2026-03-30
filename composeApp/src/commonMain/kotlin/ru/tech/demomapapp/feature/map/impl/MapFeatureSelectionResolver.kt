package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.impl.store.MapStore

internal fun interface MapFeatureSelectionResolver {
    fun resolve(
        mapState: MapState,
        featureKey: String,
        featureType: MapStore.FeatureType,
    ): SelectedMapFeature?
}

internal data class SelectedMapFeature(
    val title: String,
    val createdAtEpochMillis: Long,
)

internal class DefaultMapFeatureSelectionResolver : MapFeatureSelectionResolver {
    override fun resolve(
        mapState: MapState,
        featureKey: String,
        featureType: MapStore.FeatureType,
    ): SelectedMapFeature? =
        when (featureType) {
            MapStore.FeatureType.POINT -> mapState.points.findById(featureKey)?.toSelectedMapFeature()
            MapStore.FeatureType.LINE -> mapState.lines.findById(featureKey)?.toSelectedMapFeature()
            MapStore.FeatureType.POLYGON -> mapState.polygons.findById(featureKey)?.toSelectedMapFeature()
        }
}

private fun List<MapPoint>.findById(id: String): MapPoint? = firstOrNull { it.id == id }

private fun List<MapLine>.findById(id: String): MapLine? = firstOrNull { it.id == id }

private fun List<MapPolygon>.findById(id: String): MapPolygon? = firstOrNull { it.id == id }

private fun MapPoint.toSelectedMapFeature(): SelectedMapFeature =
    SelectedMapFeature(title = title, createdAtEpochMillis = createdAtEpochMillis)

private fun MapLine.toSelectedMapFeature(): SelectedMapFeature =
    SelectedMapFeature(title = title, createdAtEpochMillis = createdAtEpochMillis)

private fun MapPolygon.toSelectedMapFeature(): SelectedMapFeature =
    SelectedMapFeature(title = title, createdAtEpochMillis = createdAtEpochMillis)
