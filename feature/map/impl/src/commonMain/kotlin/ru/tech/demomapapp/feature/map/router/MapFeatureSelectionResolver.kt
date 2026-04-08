package ru.tech.demomapapp.feature.map.router

import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal fun interface MapFeatureSelectionResolver {
    fun resolve(
        points: List<MapPoint>,
        lines: List<MapLine>,
        polygons: List<MapPolygon>,
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
    ): SelectedMapFeature?
}

internal data class SelectedMapFeature(
    val title: String,
    val createdAtEpochMillis: Long,
)

internal class DefaultMapFeatureSelectionResolver : MapFeatureSelectionResolver {
    override fun resolve(
        points: List<MapPoint>,
        lines: List<MapLine>,
        polygons: List<MapPolygon>,
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
    ): SelectedMapFeature? = when (featureType) {
        MapScreenComponent.FeatureType.POINT -> points.findById(featureKey)?.toSelectedMapFeature()
        MapScreenComponent.FeatureType.LINE -> lines.findById(featureKey)?.toSelectedMapFeature()
        MapScreenComponent.FeatureType.POLYGON -> polygons.findById(featureKey)?.toSelectedMapFeature()
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
