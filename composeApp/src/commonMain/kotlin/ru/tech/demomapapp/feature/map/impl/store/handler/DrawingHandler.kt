package ru.tech.demomapapp.feature.map.impl.store.handler

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.impl.CreateMapLineInput
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.TimeProvider
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreMessage

internal class DrawingHandler(
    private val createMapLineUseCase: CreateMapLineUseCase,
    private val createMapPolygonUseCase: CreateMapPolygonUseCase,
    private val timeProvider: TimeProvider,
    private val featureIdProvider: FeatureIdProvider,
) {
    fun handleAddPosition(snapshot: MapCameraSnapshot?, onPositionAdded: (MapStoreMessage) -> Unit) {
        snapshot ?: return
        onPositionAdded(MapStoreMessage.DrawingPositionAdded(snapshot))
    }

    fun handleConfirm(
        state: MapStore.State,
        onLineCreated: (MapStoreMessage) -> Unit,
        onPolygonCreated: (MapStoreMessage) -> Unit,
    ) {
        val draft = state.shapeDrawingDraft ?: return
        val createdAt = timeProvider.currentTimeMillis()
        val id = featureIdProvider.nextId()

        when (draft.mode) {
            MapStore.DrawingMode.LINE -> {
                val line = createMapLineUseCase.create(
                    CreateMapLineInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                ) ?: return
                onLineCreated(MapStoreMessage.LineCreated(line))
            }

            MapStore.DrawingMode.POLYGON -> {
                val polygon = createMapPolygonUseCase.create(
                    CreateMapPolygonInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                ) ?: return
                onPolygonCreated(MapStoreMessage.PolygonCreated(polygon))
            }
        }
    }
}
