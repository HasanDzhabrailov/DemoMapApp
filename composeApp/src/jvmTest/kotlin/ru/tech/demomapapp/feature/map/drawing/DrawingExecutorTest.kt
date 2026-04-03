package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.mvikotlin.core.store.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase

class DrawingExecutorTest {

    @Test
    fun `create point confirm emits created message and label`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { input ->
                MapPoint(
                    id = input.id,
                    latitude = 55.75,
                    longitude = 37.61,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "point-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                createPointDraft = CreatePointDraft(
                    latitudeInput = "55.75",
                    longitudeInput = "37.61",
                    titleInput = "Test point",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.PointConfirmed)

        assertEquals(1, callbacks.messages.size)
        val message = callbacks.messages[0] as DrawingStore.Message.PointCreated
        assertEquals("point-1", message.point.id)
        assertEquals(55.75, message.point.latitude)
        assertEquals(37.61, message.point.longitude)
        assertEquals("Test point", message.point.title)
        assertEquals(123L, message.point.createdAtEpochMillis)

        assertEquals(1, callbacks.labels.size)
        val label = callbacks.labels[0] as DrawingStore.Label.FeatureCreated.Point
        assertEquals("point-1", label.point.id)
    }

    @Test
    fun `create point confirm with invalid input emits no message`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "point-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                createPointDraft = CreatePointDraft(
                    latitudeInput = "invalid",
                    longitudeInput = "invalid",
                    titleInput = "Test",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.PointConfirmed)

        assertEquals(0, callbacks.messages.size)
        assertEquals(0, callbacks.labels.size)
    }

    @Test
    fun `create line confirm emits created message and label`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { input ->
                MapLine(
                    id = input.id,
                    vertices = input.vertices,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 456L },
            featureIdProvider = { "line-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                shapeDrawingDraft = ShapeDrawingDraft(
                    mode = DrawingMode.LINE,
                    fixedVertices = listOf(
                        MapVertex(55.0, 37.0),
                        MapVertex(55.1, 37.1),
                    ),
                    titleInput = "Test line",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.ShapeConfirmed)

        assertEquals(1, callbacks.messages.size)
        val message = callbacks.messages[0] as DrawingStore.Message.LineCreated
        assertEquals("line-1", message.line.id)
        assertEquals(2, message.line.vertices.size)
        assertEquals("Test line", message.line.title)
        assertEquals(456L, message.line.createdAtEpochMillis)

        assertEquals(1, callbacks.labels.size)
        val label = callbacks.labels[0] as DrawingStore.Label.FeatureCreated.Line
        assertEquals("line-1", label.line.id)
    }

    @Test
    fun `create polygon confirm emits created message and label`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { input ->
                MapPolygon(
                    id = input.id,
                    vertices = input.vertices,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            timeProvider = { 789L },
            featureIdProvider = { "polygon-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                shapeDrawingDraft = ShapeDrawingDraft(
                    mode = DrawingMode.POLYGON,
                    fixedVertices = listOf(
                        MapVertex(55.0, 37.0),
                        MapVertex(55.1, 37.1),
                        MapVertex(55.2, 37.2),
                    ),
                    titleInput = "Test polygon",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.ShapeConfirmed)

        assertEquals(1, callbacks.messages.size)
        val message = callbacks.messages[0] as DrawingStore.Message.PolygonCreated
        assertEquals("polygon-1", message.polygon.id)
        assertEquals(3, message.polygon.vertices.size)
        assertEquals("Test polygon", message.polygon.title)
        assertEquals(789L, message.polygon.createdAtEpochMillis)

        assertEquals(1, callbacks.labels.size)
        val label = callbacks.labels[0] as DrawingStore.Label.FeatureCreated.Polygon
        assertEquals("polygon-1", label.polygon.id)
    }

    @Test
    fun `create line confirm without draft emits no message`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { input ->
                MapLine(
                    id = input.id,
                    vertices = input.vertices,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "line-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.ShapeConfirmed)

        assertEquals(0, callbacks.messages.size)
        assertEquals(0, callbacks.labels.size)
    }

    @Test
    fun `camera position updated stores snapshot`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "id-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(),
        )

        executor.init(callbacks)
        executor.executeIntent(
            DrawingStore.Intent.CameraPositionUpdated(
                MapCameraSnapshot(
                    latitude = 55.75,
                    longitude = 37.61,
                    zoom = 10.0,
                    bearing = 0.0,
                ),
            ),
        )

        assertEquals(1, callbacks.messages.size)
        val message = callbacks.messages[0] as DrawingStore.Message.CameraPositionUpdated
        assertEquals(55.75, message.snapshot.latitude)
        assertEquals(37.61, message.snapshot.longitude)
    }

    @Test
    fun `camera position updated initializes point draft when sheet is visible`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "id-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                isCreatePointSheetVisible = true,
                createPointDraft = null,
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(
            DrawingStore.Intent.CameraPositionUpdated(
                MapCameraSnapshot(
                    latitude = 55.75,
                    longitude = 37.61,
                    zoom = 10.0,
                    bearing = 0.0,
                ),
            ),
        )

        assertEquals(2, callbacks.messages.size)
        assertTrue(callbacks.messages[0] is DrawingStore.Message.CameraPositionUpdated)
        val draftMessage = callbacks.messages[1] as DrawingStore.Message.CreatePointDraftInitialized
        assertEquals("55.75", draftMessage.draft.latitudeInput)
        assertEquals("37.61", draftMessage.draft.longitudeInput)
    }

    @Test
    fun `drawing add position clicked emits message with camera snapshot`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "id-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                lastCameraSnapshot = MapCameraSnapshot(
                    latitude = 55.75,
                    longitude = 37.61,
                    zoom = 10.0,
                    bearing = 0.0,
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.DrawingAddPositionClicked)

        assertEquals(1, callbacks.messages.size)
        val message = callbacks.messages[0] as DrawingStore.Message.DrawingPositionAdded
        assertEquals(55.75, message.snapshot.latitude)
        assertEquals(37.61, message.snapshot.longitude)
    }

    @Test
    fun `drawing add position clicked without camera snapshot emits no message`() {
        val executor = DrawingExecutor(
            createMapPointUseCase = CreateMapPointUseCase { null },
            createMapLineUseCase = CreateMapLineUseCase { null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { null },
            timeProvider = { 123L },
            featureIdProvider = { "id-1" },
        )
        val callbacks = TestExecutorCallbacks(
            state = DrawingStore.State(
                lastCameraSnapshot = null,
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(DrawingStore.Intent.DrawingAddPositionClicked)

        assertEquals(0, callbacks.messages.size)
    }

    private class TestExecutorCallbacks(
        override var state: DrawingStore.State = DrawingStore.State(),
    ) : Executor.Callbacks<DrawingStore.State, DrawingStore.Message, Nothing, DrawingStore.Label> {
        val messages = mutableListOf<DrawingStore.Message>()
        val labels = mutableListOf<DrawingStore.Label>()

        override fun onMessage(message: DrawingStore.Message) {
            messages.add(message)
        }

        override fun onLabel(label: DrawingStore.Label) {
            labels.add(label)
        }

        override fun onAction(action: Nothing) = Unit
    }
}
