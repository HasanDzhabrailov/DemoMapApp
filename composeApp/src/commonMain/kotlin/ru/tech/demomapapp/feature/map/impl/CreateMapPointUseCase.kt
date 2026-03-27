package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapPoint

internal fun interface CreateMapPointUseCase {
    fun create(input: CreateMapPointInput): MapPoint?
}

internal data class CreateMapPointInput(
    val id: String,
    val latitudeInput: String,
    val longitudeInput: String,
    val titleInput: String,
    val createdAtEpochMillis: Long,
)

internal class DefaultCreateMapPointUseCase : CreateMapPointUseCase {
    override fun create(input: CreateMapPointInput): MapPoint? {
        val latitude = input.latitudeInput.toCoordinateOrNull() ?: return null
        val longitude = input.longitudeInput.toCoordinateOrNull() ?: return null

        return MapPoint(
            id = input.id,
            latitude = latitude,
            longitude = longitude,
            title = input.titleInput.trim().ifBlank { "Точка" },
            createdAtEpochMillis = input.createdAtEpochMillis,
        )
    }
}

private fun String.toCoordinateOrNull(): Double? =
    trim()
        .replace(',', '.')
        .toDoubleOrNull()
