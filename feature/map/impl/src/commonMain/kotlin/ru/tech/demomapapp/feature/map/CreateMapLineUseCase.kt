package ru.tech.demomapapp.feature.map

import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapVertex

internal fun interface CreateMapLineUseCase {
    fun create(input: CreateMapLineInput): MapLine?
}

internal data class CreateMapLineInput(
    val id: String,
    val vertices: List<MapVertex>,
    val titleInput: String,
    val createdAtEpochMillis: Long,
)

internal class DefaultCreateMapLineUseCase : CreateMapLineUseCase {
    override fun create(input: CreateMapLineInput): MapLine? {
        if (input.vertices.size < 2) {
            return null
        }

        return MapLine(
            id = input.id,
            vertices = input.vertices,
            title = input.titleInput.trim(),
            createdAtEpochMillis = input.createdAtEpochMillis,
        )
    }
}
