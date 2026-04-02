package ru.tech.demomapapp.feature.map.impl.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapCatalogItemKind
import ru.tech.demomapapp.feature.map.api.MapLayerCatalog
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLayerSourceRef
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
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
    fun `feature info window open closes conflicting overlays in reducer`() {
        val initialState = MapStore.State(
            isMapToolsMenuVisible = true,
            isCenterMarkerMenuVisible = true,
        )

        val updatedState = reduce(
            initialState,
            MapStoreMessage.FeatureInfoWindowOpened(
                MapStore.FeatureInfoWindow(
                    title = "Test",
                    createdAtText = "26.03.2026 10:00",
                    anchor = MapStore.FeatureInfoWindowAnchor(screenX = 10, screenY = 20),
                ),
            ),
        )

        assertFalse(updatedState.isMapToolsMenuVisible)
        assertFalse(updatedState.isCenterMarkerMenuVisible)
        assertEquals("Test", updatedState.selectedFeatureInfoWindow?.title)
    }

    @Test
    fun `available map confirm adds overlay layer and opens loaded layers sheet`() {
        val selectedMap = MapLayerCatalog.items().first { it.kind == MapCatalogItemKind.OVERLAY_LAYER }
        val initialState = MapStore.State(
            isAvailableMapsSheetVisible = true,
            selectedAvailableMap = selectedMap,
        )

        val updatedState = reduce(initialState, MapStoreMessage.AvailableMapConfirmed)

        assertEquals(1, updatedState.mapState.overlayLayers.size)
        assertEquals(selectedMap.title, updatedState.mapState.overlayLayers.single().title)
        assertTrue(updatedState.isMapsOnScreenSheetVisible)
        assertFalse(updatedState.isAvailableMapsSheetVisible)
    }

    @Test
    fun `overlay layer opacity change updates map state`() {
        val layer = MapStore.State().availableMapCatalog.first { it.kind == MapCatalogItemKind.OVERLAY_LAYER }
        val source = layer.source as MapLayerSourceRef.RasterTileTemplate
        val initialState = MapStore.State(
            mapState = MapStore.State().mapState.copy(
                overlayLayers = listOf(
                    MapLayerEntry(
                        id = layer.id,
                        title = layer.title,
                        source = source,
                        opacity = 0.5f,
                    ),
                ),
            ),
            editingOverlayOpacityLayer = MapLayerEntry(
                id = layer.id,
                title = layer.title,
                source = source,
                opacity = 0.5f,
            ),
        )

        val updatedState = reduce(initialState, MapStoreMessage.OverlayLayerOpacityChanged(0.8f))

        assertEquals(0.8f, updatedState.mapState.overlayLayers.single().opacity)
        assertEquals(0.8f, updatedState.editingOverlayOpacityLayer?.opacity)
    }

    @Test
    fun `overlay layer move up follows visible stack order`() {
        val source = MapLayerSourceRef.RasterTileTemplate(templateId = "dem-overlay")
        val bottomLayer = MapLayerEntry(id = "bottom", title = "Bottom", source = source)
        val topLayer = MapLayerEntry(id = "top", title = "Top", source = source)
        val initialState = MapStore.State(
            mapState = MapStore.State().mapState.copy(
                overlayLayers = listOf(bottomLayer, topLayer),
            ),
            selectedOverlayLayer = bottomLayer,
        )

        val updatedState = reduce(initialState, MapStoreMessage.OverlayLayerMovedUp)

        assertEquals(listOf("top", "bottom"), updatedState.mapState.overlayLayers.map(MapLayerEntry::id))
        assertEquals("bottom", updatedState.selectedOverlayLayer?.id)
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

    private fun reduce(state: MapStore.State, message: MapStoreMessage): MapStore.State =
        with(MapStoreReducer(DefaultShapeDrawingDraftUpdater())) { state.reduce(message) }

    private fun vertex(latitude: Double, longitude: Double) = MapVertex(latitude = latitude, longitude = longitude)
}
