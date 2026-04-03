package ru.tech.demomapapp.feature.map.impl.drawing

internal object DrawingReducer {
    @Suppress("ReturnCount")
    fun reduce(state: DrawingStore.State, message: DrawingStore.Message): DrawingStore.State {
        return when (message) {
            is DrawingStore.Message.CreatePointSheetOpened -> {
                state.copy(
                    isCreatePointSheetVisible = true,
                )
            }

            is DrawingStore.Message.PointLatitudeUpdated -> {
                val draft = state.createPointDraft ?: return state
                state.copy(
                    createPointDraft = draft.copy(latitudeInput = message.value),
                )
            }

            is DrawingStore.Message.PointLongitudeUpdated -> {
                val draft = state.createPointDraft ?: return state
                state.copy(
                    createPointDraft = draft.copy(longitudeInput = message.value),
                )
            }

            is DrawingStore.Message.PointTitleUpdated -> {
                val draft = state.createPointDraft ?: return state
                state.copy(
                    createPointDraft = draft.copy(titleInput = message.value),
                )
            }

            is DrawingStore.Message.PointCreated -> {
                state.copy(
                    points = state.points + message.point,
                    isCreatePointSheetVisible = false,
                    createPointDraft = null,
                )
            }

            is DrawingStore.Message.CreatePointSheetClosed -> {
                state.copy(
                    isCreatePointSheetVisible = false,
                    createPointDraft = null,
                )
            }

            is DrawingStore.Message.DrawingModeEntered -> {
                state.copy(
                    drawingMode = message.mode,
                    shapeDrawingDraft = ShapeDrawingDraft(mode = message.mode),
                )
            }

            is DrawingStore.Message.DrawingPositionAdded -> {
                val draft = state.shapeDrawingDraft ?: return state
                state.copy(
                    shapeDrawingDraft = draft.copy(
                        fixedVertices = draft.fixedVertices + message.snapshot.toVertex(),
                    ),
                )
            }

            is DrawingStore.Message.DrawingLastPositionRemoved -> {
                val draft = state.shapeDrawingDraft ?: return state
                state.copy(
                    shapeDrawingDraft = draft.copy(
                        fixedVertices = draft.fixedVertices.dropLast(1),
                    ),
                )
            }

            is DrawingStore.Message.ShapeTitleUpdated -> {
                val draft = state.shapeDrawingDraft ?: return state
                state.copy(
                    shapeDrawingDraft = draft.copy(titleInput = message.value),
                )
            }

            is DrawingStore.Message.ShapeSheetOpened -> {
                val draft = state.shapeDrawingDraft ?: return state
                if (draft.fixedVertices.size >= draft.mode.minimumVertexCount()) {
                    state.copy(isCreateShapeSheetVisible = true)
                } else {
                    state
                }
            }

            is DrawingStore.Message.LineCreated -> {
                state.copy(
                    lines = state.lines + message.line,
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                )
            }

            is DrawingStore.Message.PolygonCreated -> {
                state.copy(
                    polygons = state.polygons + message.polygon,
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                )
            }

            is DrawingStore.Message.ShapeSheetClosed -> {
                state.copy(
                    isCreateShapeSheetVisible = false,
                )
            }

            is DrawingStore.Message.DrawingModeExited -> {
                state.copy(
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                )
            }

            is DrawingStore.Message.CreatePointDraftInitialized -> {
                state.copy(
                    createPointDraft = message.draft,
                )
            }

            is DrawingStore.Message.CameraPositionUpdated -> {
                state.copy(
                    lastCameraSnapshot = message.snapshot,
                )
            }
        }
    }

    private fun DrawingMode.minimumVertexCount(): Int = when (this) {
        DrawingMode.LINE -> 2
        DrawingMode.POLYGON -> 3
    }
}
