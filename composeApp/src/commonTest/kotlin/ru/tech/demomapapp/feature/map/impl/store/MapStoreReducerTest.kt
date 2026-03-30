package ru.tech.demomapapp.feature.map.impl.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapVertex

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

    private fun reduce(state: MapStore.State, message: MapStoreMessage): MapStore.State =
        with(MapStoreReducer) { state.reduce(message) }

    private fun vertex(latitude: Double, longitude: Double) =
        MapVertex(latitude = latitude, longitude = longitude)
}
