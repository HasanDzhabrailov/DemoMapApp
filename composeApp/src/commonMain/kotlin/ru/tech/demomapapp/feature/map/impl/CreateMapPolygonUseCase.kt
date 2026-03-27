package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapVertex

internal fun interface CreateMapPolygonUseCase {
    fun create(input: CreateMapPolygonInput): MapPolygon?
}

internal data class CreateMapPolygonInput(
    val id: String,
    val vertices: List<MapVertex>,
    val titleInput: String,
    val createdAtEpochMillis: Long,
)

internal class DefaultCreateMapPolygonUseCase : CreateMapPolygonUseCase {
    override fun create(input: CreateMapPolygonInput): MapPolygon? {
        if (input.vertices.size < 3) {
            return null
        }

        return MapPolygon(
            id = input.id,
            vertices = input.vertices,
            title = input.titleInput.trim(),
            createdAtEpochMillis = input.createdAtEpochMillis,
        )
    }
}
