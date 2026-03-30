package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.impl.store.MapStore

internal interface ShapeDrawingDraftUpdater {
    fun addVertex(
        draft: MapScreenComponent.ShapeDrawingDraft,
        snapshot: MapCameraSnapshot,
    ): MapScreenComponent.ShapeDrawingDraft

    fun addVertex(
        draft: MapStore.ShapeDrawingDraft,
        snapshot: MapCameraSnapshot,
    ): MapStore.ShapeDrawingDraft

    fun removeLastVertex(draft: MapScreenComponent.ShapeDrawingDraft): MapScreenComponent.ShapeDrawingDraft

    fun removeLastVertex(draft: MapStore.ShapeDrawingDraft): MapStore.ShapeDrawingDraft
}

internal class DefaultShapeDrawingDraftUpdater : ShapeDrawingDraftUpdater {
    override fun addVertex(
        draft: MapScreenComponent.ShapeDrawingDraft,
        snapshot: MapCameraSnapshot,
    ): MapScreenComponent.ShapeDrawingDraft =
        draft.copy(
            fixedVertices = draft.fixedVertices + snapshot.toVertex(),
        )

    override fun addVertex(
        draft: MapStore.ShapeDrawingDraft,
        snapshot: MapCameraSnapshot,
    ): MapStore.ShapeDrawingDraft =
        draft.copy(
            fixedVertices = draft.fixedVertices + snapshot.toVertex(),
        )

    override fun removeLastVertex(draft: MapScreenComponent.ShapeDrawingDraft): MapScreenComponent.ShapeDrawingDraft =
        draft.copy(
            fixedVertices = draft.fixedVertices.dropLast(1),
        )

    override fun removeLastVertex(draft: MapStore.ShapeDrawingDraft): MapStore.ShapeDrawingDraft =
        draft.copy(
            fixedVertices = draft.fixedVertices.dropLast(1),
        )
}

internal fun MapCameraSnapshot.toVertex(): MapVertex =
    MapVertex(
        latitude = latitude,
        longitude = longitude,
    )
