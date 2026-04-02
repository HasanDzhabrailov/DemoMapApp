package ru.tech.demomapapp.feature.map.impl.drawing

import ru.tech.demomapapp.feature.map.impl.CreateMapLineInput
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase

internal class DrawingExecutor(
    private val createMapPointUseCase: CreateMapPointUseCase,
    private val createMapLineUseCase: CreateMapLineUseCase,
    private val createMapPolygonUseCase: CreateMapPolygonUseCase,
    private val timeProvider: () -> Long,
    private val featureIdProvider: () -> String,
) : com.arkivanov.mvikotlin.core.store.Executor<
    DrawingStore.Intent,
    Nothing,
    DrawingStore.State,
    DrawingStore.Message,
    DrawingStore.Label,
    > {

    private lateinit var callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
        DrawingStore.State,
        DrawingStore.Message,
        Nothing,
        DrawingStore.Label,
        >

    override fun init(
        callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
            DrawingStore.State,
            DrawingStore.Message,
            Nothing,
            DrawingStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: DrawingStore.Intent) {
        when (intent) {
            is DrawingStore.Intent.CreatePointClicked -> {
                callbacks.onMessage(DrawingStore.Message.CreatePointSheetOpened)
            }

            is DrawingStore.Intent.PointLatitudeChanged -> {
                callbacks.onMessage(DrawingStore.Message.PointLatitudeUpdated(intent.value))
            }

            is DrawingStore.Intent.PointLongitudeChanged -> {
                callbacks.onMessage(DrawingStore.Message.PointLongitudeUpdated(intent.value))
            }

            is DrawingStore.Intent.PointTitleChanged -> {
                callbacks.onMessage(DrawingStore.Message.PointTitleUpdated(intent.value))
            }

            is DrawingStore.Intent.PointConfirmed -> {
                handlePointConfirm()
            }

            is DrawingStore.Intent.PointSheetDismissed -> {
                callbacks.onMessage(DrawingStore.Message.CreatePointSheetClosed)
            }

            is DrawingStore.Intent.CreateLineClicked -> {
                callbacks.onMessage(DrawingStore.Message.DrawingModeEntered(DrawingMode.LINE))
            }

            is DrawingStore.Intent.CreatePolygonClicked -> {
                callbacks.onMessage(DrawingStore.Message.DrawingModeEntered(DrawingMode.POLYGON))
            }

            is DrawingStore.Intent.DrawingAddPositionClicked -> {
                val state = callbacks.state
                val snapshot = state.lastCameraSnapshot ?: return
                callbacks.onMessage(DrawingStore.Message.DrawingPositionAdded(snapshot))
            }

            is DrawingStore.Intent.DrawingRemoveLastPositionClicked -> {
                callbacks.onMessage(DrawingStore.Message.DrawingLastPositionRemoved)
            }

            is DrawingStore.Intent.ShapeTitleChanged -> {
                callbacks.onMessage(DrawingStore.Message.ShapeTitleUpdated(intent.value))
            }

            is DrawingStore.Intent.ShapeConfirmed -> {
                handleShapeConfirm()
            }

            is DrawingStore.Intent.ShapeSheetDismissed -> {
                callbacks.onMessage(DrawingStore.Message.ShapeSheetClosed)
            }

            is DrawingStore.Intent.DrawingDismissed -> {
                callbacks.onMessage(DrawingStore.Message.DrawingModeExited)
            }

            is DrawingStore.Intent.CameraPositionUpdated -> {
                callbacks.onMessage(DrawingStore.Message.CameraPositionUpdated(intent.snapshot))
                if (callbacks.state.isCreatePointSheetVisible && callbacks.state.createPointDraft == null) {
                    val draft = CreatePointDraft(
                        latitudeInput = intent.snapshot.latitude.toString(),
                        longitudeInput = intent.snapshot.longitude.toString(),
                        titleInput = "",
                    )
                    callbacks.onMessage(DrawingStore.Message.CreatePointDraftInitialized(draft))
                }
            }
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun handlePointConfirm() {
        val state = callbacks.state
        val draft = state.createPointDraft ?: return
        val point = createMapPointUseCase.create(
            CreateMapPointInput(
                id = featureIdProvider(),
                latitudeInput = draft.latitudeInput,
                longitudeInput = draft.longitudeInput,
                titleInput = draft.titleInput,
                createdAtEpochMillis = timeProvider(),
            ),
        )
        if (point != null) {
            callbacks.onMessage(DrawingStore.Message.PointCreated(point))
            callbacks.onLabel(DrawingStore.Label.FeatureCreated.Point(point))
        }
    }

    private fun handleShapeConfirm() {
        val state = callbacks.state
        val draft = state.shapeDrawingDraft ?: return
        val createdAt = timeProvider()
        val id = featureIdProvider()

        when (draft.mode) {
            DrawingMode.LINE -> {
                val line = createMapLineUseCase.create(
                    CreateMapLineInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                )
                if (line != null) {
                    callbacks.onMessage(DrawingStore.Message.LineCreated(line))
                    callbacks.onLabel(DrawingStore.Label.FeatureCreated.Line(line))
                }
            }

            DrawingMode.POLYGON -> {
                val polygon = createMapPolygonUseCase.create(
                    CreateMapPolygonInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                )
                if (polygon != null) {
                    callbacks.onMessage(DrawingStore.Message.PolygonCreated(polygon))
                    callbacks.onLabel(DrawingStore.Label.FeatureCreated.Polygon(polygon))
                }
            }
        }
    }
}
