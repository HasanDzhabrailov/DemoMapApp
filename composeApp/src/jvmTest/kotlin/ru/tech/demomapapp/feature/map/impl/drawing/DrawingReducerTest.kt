package ru.tech.demomapapp.feature.map.impl.drawing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapVertex

class DrawingReducerTest {

    @Test
    fun `create point sheet opened sets visibility`() {
        val state = DrawingStore.State()
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.CreatePointSheetOpened)

        assertEquals(true, newState.isCreatePointSheetVisible)
    }

    @Test
    fun `point latitude updated changes draft`() {
        val state = DrawingStore.State(
            createPointDraft = CreatePointDraft(
                latitudeInput = "55.0",
                longitudeInput = "37.0",
                titleInput = "Test",
            ),
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.PointLatitudeUpdated("55.75"))

        assertEquals("55.75", newState.createPointDraft?.latitudeInput)
        assertEquals("37.0", newState.createPointDraft?.longitudeInput)
        assertEquals("Test", newState.createPointDraft?.titleInput)
    }

    @Test
    fun `point created adds point and clears sheet`() {
        val point = MapPoint(
            id = "point-1",
            latitude = 55.75,
            longitude = 37.61,
            title = "Test",
            createdAtEpochMillis = 123L,
        )
        val state = DrawingStore.State(
            isCreatePointSheetVisible = true,
            createPointDraft = CreatePointDraft("55.75", "37.61", "Test"),
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.PointCreated(point))

        assertEquals(1, newState.points.size)
        assertEquals(point, newState.points[0])
        assertEquals(false, newState.isCreatePointSheetVisible)
        assertNull(newState.createPointDraft)
    }

    @Test
    fun `drawing mode entered sets mode and creates draft`() {
        val state = DrawingStore.State()
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.DrawingModeEntered(DrawingMode.LINE))

        assertEquals(DrawingMode.LINE, newState.drawingMode)
        assertEquals(DrawingMode.LINE, newState.shapeDrawingDraft?.mode)
        assertTrue(newState.shapeDrawingDraft?.fixedVertices?.isEmpty() == true)
    }

    @Test
    fun `drawing last position removed drops last vertex`() {
        val state = DrawingStore.State(
            shapeDrawingDraft = ShapeDrawingDraft(
                mode = DrawingMode.LINE,
                fixedVertices = listOf(
                    MapVertex(55.0, 37.0),
                    MapVertex(55.1, 37.1),
                    MapVertex(55.2, 37.2),
                ),
            ),
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.DrawingLastPositionRemoved)

        assertEquals(2, newState.shapeDrawingDraft?.fixedVertices?.size)
        assertEquals(MapVertex(55.0, 37.0), newState.shapeDrawingDraft?.fixedVertices?.get(0))
        assertEquals(MapVertex(55.1, 37.1), newState.shapeDrawingDraft?.fixedVertices?.get(1))
    }

    @Test
    fun `line created adds line and exits drawing mode`() {
        val line = MapLine(
            id = "line-1",
            vertices = listOf(MapVertex(55.0, 37.0), MapVertex(55.1, 37.1)),
            title = "Test line",
            createdAtEpochMillis = 123L,
        )
        val state = DrawingStore.State(
            drawingMode = DrawingMode.LINE,
            shapeDrawingDraft = ShapeDrawingDraft(DrawingMode.LINE),
            isCreateShapeSheetVisible = true,
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.LineCreated(line))

        assertEquals(1, newState.lines.size)
        assertEquals(line, newState.lines[0])
        assertNull(newState.drawingMode)
        assertNull(newState.shapeDrawingDraft)
        assertEquals(false, newState.isCreateShapeSheetVisible)
    }

    @Test
    fun `polygon created adds polygon and exits drawing mode`() {
        val polygon = MapPolygon(
            id = "polygon-1",
            vertices = listOf(
                MapVertex(55.0, 37.0),
                MapVertex(55.1, 37.1),
                MapVertex(55.2, 37.2),
            ),
            title = "Test polygon",
            createdAtEpochMillis = 123L,
        )
        val state = DrawingStore.State(
            drawingMode = DrawingMode.POLYGON,
            shapeDrawingDraft = ShapeDrawingDraft(DrawingMode.POLYGON),
            isCreateShapeSheetVisible = true,
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.PolygonCreated(polygon))

        assertEquals(1, newState.polygons.size)
        assertEquals(polygon, newState.polygons[0])
        assertNull(newState.drawingMode)
        assertNull(newState.shapeDrawingDraft)
        assertEquals(false, newState.isCreateShapeSheetVisible)
    }

    @Test
    fun `create point draft initialized sets draft`() {
        val draft = CreatePointDraft(
            latitudeInput = "55.75",
            longitudeInput = "37.61",
            titleInput = "",
        )
        val state = DrawingStore.State()
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.CreatePointDraftInitialized(draft))

        assertEquals(draft, newState.createPointDraft)
    }

    @Test
    fun `drawing mode exited clears all drawing state`() {
        val state = DrawingStore.State(
            drawingMode = DrawingMode.LINE,
            shapeDrawingDraft = ShapeDrawingDraft(DrawingMode.LINE),
            isCreateShapeSheetVisible = true,
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.DrawingModeExited)

        assertNull(newState.drawingMode)
        assertNull(newState.shapeDrawingDraft)
        assertEquals(false, newState.isCreateShapeSheetVisible)
    }

    @Test
    fun `drawing position added adds vertex from camera snapshot`() {
        val state = DrawingStore.State(
            shapeDrawingDraft = ShapeDrawingDraft(
                mode = DrawingMode.LINE,
                fixedVertices = listOf(MapVertex(55.0, 37.0)),
            ),
        )
        val snapshot = MapCameraSnapshot(
            latitude = 55.75,
            longitude = 37.61,
            zoom = 10.0,
            bearing = 0.0,
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.DrawingPositionAdded(snapshot))

        assertEquals(2, newState.shapeDrawingDraft?.fixedVertices?.size)
        assertEquals(MapVertex(55.0, 37.0), newState.shapeDrawingDraft?.fixedVertices?.get(0))
        assertEquals(MapVertex(55.75, 37.61), newState.shapeDrawingDraft?.fixedVertices?.get(1))
    }

    @Test
    fun `camera position updated stores snapshot`() {
        val state = DrawingStore.State()
        val snapshot = MapCameraSnapshot(
            latitude = 55.75,
            longitude = 37.61,
            zoom = 10.0,
            bearing = 0.0,
        )
        val newState = DrawingReducer.reduce(state, DrawingStore.Message.CameraPositionUpdated(snapshot))

        assertEquals(snapshot, newState.lastCameraSnapshot)
    }
}
