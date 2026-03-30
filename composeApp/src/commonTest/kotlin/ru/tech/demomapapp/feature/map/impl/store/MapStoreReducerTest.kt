package ru.tech.demomapapp.feature.map.impl.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.impl.DefaultShapeDrawingDraftUpdater

class MapStoreReducerTest {

    @Test
    fun `map tools toggle closes center marker menu and info window when opening`() {
        val initialState = MapStore.State(
            isCenterMarkerMenuVisible = true,
            selectedFeatureInfoWindow = MapStore.FeatureInfoWindow(
                title = "Test",
                createdAtText = "26.03.2026 10:00",
                anchor = MapStore.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val updatedState = reduce(initialState, MapStoreMessage.MapToolsMenuToggled)

        assertTrue(updatedState.isMapToolsMenuVisible)
        assertFalse(updatedState.isCenterMarkerMenuVisible)
        assertNull(updatedState.selectedFeatureInfoWindow)
    }

    @Test
    fun `create point sheet open uses last camera snapshot as draft`() {
        val initialState = MapStore.State(
            lastCameraSnapshot = MapCameraSnapshot(
                latitude = 55.75,
                longitude = 37.61,
                zoom = 12.0,
                bearing = 0.0,
            ),
            isMapToolsMenuVisible = true,
            isCenterMarkerMenuVisible = true,
        )

        val updatedState = reduce(initialState, MapStoreMessage.CreatePointSheetOpened)

        assertFalse(updatedState.isMapToolsMenuVisible)
        assertFalse(updatedState.isCenterMarkerMenuVisible)
        assertTrue(updatedState.isCreatePointSheetVisible)
        assertEquals("55.75", updatedState.createPointDraft?.latitudeInput)
        assertEquals("37.61", updatedState.createPointDraft?.longitudeInput)
    }

    @Test
    fun `create point field updates stay reducer owned`() {
        val initialState = MapStore.State(
            createPointDraft = MapStore.CreatePointDraft(
                latitudeInput = "55.75",
                longitudeInput = "37.61",
            ),
        )

        val updatedState = reduce(
            reduce(
                reduce(initialState, MapStoreMessage.CreatePointLatitudeChanged("59.0")),
                MapStoreMessage.CreatePointLongitudeChanged("30.0"),
            ),
            MapStoreMessage.CreatePointTitleChanged("Test point"),
        )

        assertEquals("59.0", updatedState.createPointDraft?.latitudeInput)
        assertEquals("30.0", updatedState.createPointDraft?.longitudeInput)
        assertEquals("Test point", updatedState.createPointDraft?.titleInput)
    }

    @Test
    fun `create point created appends point and clears sheet state`() {
        val initialState = MapStore.State(
            isCreatePointSheetVisible = true,
            createPointDraft = MapStore.CreatePointDraft(
                latitudeInput = "55.75",
                longitudeInput = "37.61",
                titleInput = "Test point",
            ),
        )

        val updatedState = reduce(
            initialState,
            MapStoreMessage.CreatePointCreated(
                MapPoint(
                    id = "point-1",
                    latitude = 55.75,
                    longitude = 37.61,
                    title = "Test point",
                    createdAtEpochMillis = 1L,
                ),
            ),
        )

        assertEquals(1, updatedState.mapState.points.size)
        assertFalse(updatedState.isCreatePointSheetVisible)
        assertNull(updatedState.createPointDraft)
    }

    @Test
    fun `camera idle stores snapshot and clears feature info window`() {
        val initialState = MapStore.State(
            selectedFeatureInfoWindow = MapStore.FeatureInfoWindow(
                title = "Test",
                createdAtText = "26.03.2026 10:00",
                anchor = MapStore.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val updatedState = reduce(
            initialState,
            MapStoreMessage.CameraIdleReceived(
                MapCameraSnapshot(
                    latitude = 55.75,
                    longitude = 37.61,
                    zoom = 12.0,
                    bearing = 0.0,
                ),
            ),
        )

        assertEquals(55.75, updatedState.lastCameraSnapshot?.latitude)
        assertNull(updatedState.selectedFeatureInfoWindow)
    }

    @Test
    fun `drawing start resets point flow and opens draft`() {
        val initialState = MapStore.State(
            isCreatePointSheetVisible = true,
            createPointDraft = MapStore.CreatePointDraft(
                latitudeInput = "55.75",
                longitudeInput = "37.61",
                titleInput = "Point",
            ),
        )

        val updatedState = reduce(initialState, MapStoreMessage.DrawingStarted(MapStore.DrawingMode.LINE))

        assertFalse(updatedState.isCreatePointSheetVisible)
        assertNull(updatedState.createPointDraft)
        assertEquals(MapStore.DrawingMode.LINE, updatedState.drawingMode)
        assertEquals(MapStore.DrawingMode.LINE, updatedState.shapeDrawingDraft?.mode)
    }

    @Test
    fun `shape sheet opens only when draft has enough vertices`() {
        val initialDraft = MapStore.ShapeDrawingDraft(
            mode = MapStore.DrawingMode.POLYGON,
            fixedVertices = listOf(
                vertex(55.75, 37.61),
                vertex(55.76, 37.62),
            ),
        )
        val initialState = MapStore.State(
            shapeDrawingDraft = initialDraft,
        )

        val unchangedState = reduce(initialState, MapStoreMessage.ShapeSheetOpened)
        assertFalse(unchangedState.isCreateShapeSheetVisible)

        val readyState = reduce(
            initialState.copy(
                shapeDrawingDraft = initialDraft.copy(
                    fixedVertices = initialDraft.fixedVertices + vertex(55.77, 37.63),
                ),
            ),
            MapStoreMessage.ShapeSheetOpened,
        )

        assertTrue(readyState.isCreateShapeSheetVisible)
    }

    @Test
    fun `drawing position added updates draft in reducer and clears info window`() {
        val initialState = MapStore.State(
            shapeDrawingDraft = MapStore.ShapeDrawingDraft(
                mode = MapStore.DrawingMode.LINE,
                fixedVertices = listOf(vertex(55.75, 37.61)),
            ),
            selectedFeatureInfoWindow = MapStore.FeatureInfoWindow(
                title = "Test",
                createdAtText = "26.03.2026 10:00",
                anchor = MapStore.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val addedVertexState = reduce(
            initialState,
            MapStoreMessage.DrawingPositionAdded(
                MapCameraSnapshot(
                    latitude = 55.76,
                    longitude = 37.62,
                    zoom = 12.0,
                    bearing = 0.0,
                ),
            ),
        )

        assertEquals(2, addedVertexState.shapeDrawingDraft?.fixedVertices?.size)
        assertNull(addedVertexState.selectedFeatureInfoWindow)
        assertEquals(vertex(55.76, 37.62), addedVertexState.shapeDrawingDraft?.fixedVertices?.last())
    }

    @Test
    fun `drawing last position removed updates draft in reducer without clearing info window`() {
        val initialState = MapStore.State(
            shapeDrawingDraft = MapStore.ShapeDrawingDraft(
                mode = MapStore.DrawingMode.LINE,
                fixedVertices = listOf(
                    vertex(55.75, 37.61),
                    vertex(55.76, 37.62),
                ),
            ),
            selectedFeatureInfoWindow = MapStore.FeatureInfoWindow(
                title = "Test",
                createdAtText = "26.03.2026 10:00",
                anchor = MapStore.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
            ),
        )

        val removedVertexState = reduce(
            initialState,
            MapStoreMessage.DrawingLastPositionRemoved,
        )

        assertEquals(1, removedVertexState.shapeDrawingDraft?.fixedVertices?.size)
        assertNotNull(removedVertexState.selectedFeatureInfoWindow)
    }

    @Test
    fun `line created appends line and resets drawing state`() {
        val initialState = MapStore.State(
            drawingMode = MapStore.DrawingMode.LINE,
            shapeDrawingDraft = MapStore.ShapeDrawingDraft(
                mode = MapStore.DrawingMode.LINE,
                fixedVertices = listOf(
                    vertex(55.75, 37.61),
                    vertex(55.76, 37.62),
                ),
            ),
            isCreateShapeSheetVisible = true,
        )

        val updatedState = reduce(
            initialState,
            MapStoreMessage.LineCreated(
                MapLine(
                    id = "line-1",
                    vertices = listOf(
                        vertex(55.75, 37.61),
                        vertex(55.76, 37.62),
                    ),
                    title = "Route A",
                    createdAtEpochMillis = 1L,
                ),
            ),
        )

        assertEquals(1, updatedState.mapState.lines.size)
        assertNull(updatedState.shapeDrawingDraft)
        assertNull(updatedState.drawingMode)
        assertFalse(updatedState.isCreateShapeSheetVisible)
    }

    @Test
    fun `polygon created appends polygon and resets drawing state`() {
        val initialState = MapStore.State(
            drawingMode = MapStore.DrawingMode.POLYGON,
            shapeDrawingDraft = MapStore.ShapeDrawingDraft(
                mode = MapStore.DrawingMode.POLYGON,
                fixedVertices = listOf(
                    vertex(55.75, 37.61),
                    vertex(55.76, 37.62),
                    vertex(55.77, 37.63),
                ),
            ),
            isCreateShapeSheetVisible = true,
        )

        val updatedState = reduce(
            initialState,
            MapStoreMessage.PolygonCreated(
                MapPolygon(
                    id = "polygon-1",
                    vertices = listOf(
                        vertex(55.75, 37.61),
                        vertex(55.76, 37.62),
                        vertex(55.77, 37.63),
                    ),
                    title = "Area A",
                    createdAtEpochMillis = 1L,
                ),
            ),
        )

        assertEquals(1, updatedState.mapState.polygons.size)
        assertNull(updatedState.shapeDrawingDraft)
        assertNull(updatedState.drawingMode)
        assertFalse(updatedState.isCreateShapeSheetVisible)
    }

    @Test
    fun `ruler messages update dedicated ruler state`() {
        val enabledState = reduce(MapStore.State(), MapStoreMessage.RulerEnabled)
        assertTrue(enabledState.isRulerEnabled)

        val markerState = reduce(
            enabledState,
            MapStoreMessage.CurrentLocationMarkerUpdated(
                mode = MyLocationMode.MANUAL_PLACEHOLDER,
                marker = MapLocationMarker(
                    latitude = 59.0,
                    longitude = 30.0,
                    isPlaceholder = true,
                ),
            ),
        )
        assertEquals(MyLocationMode.MANUAL_PLACEHOLDER, markerState.myLocationMode)
        assertNotNull(markerState.currentLocationMarker)

        val measuredState = reduce(
            markerState,
            MapStoreMessage.RulerMeasurementUpdated(
                measurement = RulerMeasurement(
                    startLatitude = 59.0,
                    startLongitude = 30.0,
                    endLatitude = 59.1,
                    endLongitude = 30.1,
                    distanceMeters = 100.0,
                    trueAzimuthDegrees = 42.0,
                ),
                infoWindow = RulerInfoWindowState(
                    distanceText = "100 м",
                    trueAzimuthText = "A = 42°",
                    magneticAzimuthText = "Am = 40°",
                ),
            ),
        )
        assertEquals("100 м", measuredState.rulerInfoWindow?.distanceText)

        val disabledState = reduce(measuredState, MapStoreMessage.RulerDisabled)
        assertFalse(disabledState.isRulerEnabled)
        assertNull(disabledState.rulerMeasurement)
        assertNull(disabledState.rulerInfoWindow)
    }

    private fun reduce(state: MapStore.State, message: MapStoreMessage): MapStore.State =
        with(MapStoreReducer(DefaultShapeDrawingDraftUpdater())) { state.reduce(message) }

    private fun vertex(latitude: Double, longitude: Double) =
        MapVertex(latitude = latitude, longitude = longitude)
}
