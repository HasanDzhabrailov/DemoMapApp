package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.impl.CreateMapLineInput
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointInput
import ru.tech.demomapapp.feature.map.impl.DefaultMapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.DefaultMapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.DefaultRulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.DefaultRulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.MapPointCreatedAtFormatter
import ru.tech.demomapapp.feature.map.impl.TimeProvider

class MapStoreExecutorTest {

    @Test
    fun `create point confirm emits created message from executor`() {
        val executor = MapStoreExecutor(
            createMapPointUseCase = CreateMapPointUseCase { input ->
                ru.tech.demomapapp.feature.map.api.MapPoint(
                    id = input.id,
                    latitude = 55.75,
                    longitude = 37.61,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            createMapLineUseCase = CreateMapLineUseCase { _: CreateMapLineInput -> null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { _: CreateMapPolygonInput -> null },
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "point-1" },
            featureSelectionResolver = DefaultMapFeatureSelectionResolver(),
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
            rulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
        )
        val callbacks = TestExecutorCallbacks(
            state = MapStore.State(
                createPointDraft = MapStore.CreatePointDraft(
                    latitudeInput = "55.75",
                    longitudeInput = "37.61",
                    titleInput = "Test point",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(MapStore.Intent.CreatePoint.Confirmed)

        val expectedMessages: List<MapStoreMessage> = listOf(
            MapStoreMessage.CreatePointCreated(
                ru.tech.demomapapp.feature.map.api.MapPoint(
                    id = "point-1",
                    latitude = 55.75,
                    longitude = 37.61,
                    title = "Test point",
                    createdAtEpochMillis = 123L,
                ),
            ),
        )

        assertEquals(
            expectedMessages,
            callbacks.messages,
        )
    }

    @Test
    fun `create point confirm with invalid input emits no message`() {
        val executor = MapStoreExecutor(
            createMapPointUseCase = CreateMapPointUseCase { _: CreateMapPointInput -> null },
            createMapLineUseCase = CreateMapLineUseCase { _: CreateMapLineInput -> null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { _: CreateMapPolygonInput -> null },
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "point-1" },
            featureSelectionResolver = DefaultMapFeatureSelectionResolver(),
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
            rulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
        )
        val callbacks = TestExecutorCallbacks(
            state = MapStore.State(
                createPointDraft = MapStore.CreatePointDraft(
                    latitudeInput = "invalid",
                    longitudeInput = "37.61",
                    titleInput = "Test point",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(MapStore.Intent.CreatePoint.Confirmed)

        assertTrue(callbacks.messages.isEmpty())
    }

    @Test
    fun `drawing add position emits snapshot message from executor`() {
        val executor = createExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapStore.State(
                lastCameraSnapshot = MapCameraSnapshot(
                    latitude = 55.75,
                    longitude = 37.61,
                    zoom = 12.0,
                    bearing = 0.0,
                ),
                shapeDrawingDraft = MapStore.ShapeDrawingDraft(mode = MapStore.DrawingMode.LINE),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(MapStore.Intent.Drawing.AddPositionClicked)

        assertEquals(
            listOf<MapStoreMessage>(
                MapStoreMessage.DrawingPositionAdded(
                    MapCameraSnapshot(
                        latitude = 55.75,
                        longitude = 37.61,
                        zoom = 12.0,
                        bearing = 0.0,
                    ),
                ),
            ),
            callbacks.messages,
        )
    }

    @Test
    fun `shape confirm emits line created message from executor`() {
        val executor = MapStoreExecutor(
            createMapPointUseCase = CreateMapPointUseCase { _: CreateMapPointInput -> null },
            createMapLineUseCase = CreateMapLineUseCase { input ->
                MapLine(
                    id = input.id,
                    vertices = input.vertices,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            createMapPolygonUseCase = CreateMapPolygonUseCase { _: CreateMapPolygonInput -> null },
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "line-1" },
            featureSelectionResolver = DefaultMapFeatureSelectionResolver(),
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
            rulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
        )
        val callbacks = TestExecutorCallbacks(
            state = MapStore.State(
                shapeDrawingDraft = MapStore.ShapeDrawingDraft(
                    mode = MapStore.DrawingMode.LINE,
                    fixedVertices = listOf(
                        MapVertex(latitude = 55.75, longitude = 37.61),
                        MapVertex(latitude = 55.76, longitude = 37.62),
                    ),
                    titleInput = "Route A",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(MapStore.Intent.Drawing.Confirmed)

        assertEquals(
            listOf<MapStoreMessage>(
                MapStoreMessage.LineCreated(
                    MapLine(
                        id = "line-1",
                        vertices = listOf(
                            MapVertex(latitude = 55.75, longitude = 37.61),
                            MapVertex(latitude = 55.76, longitude = 37.62),
                        ),
                        title = "Route A",
                        createdAtEpochMillis = 123L,
                    ),
                ),
            ),
            callbacks.messages,
        )
    }

    @Test
    fun `shape confirm emits polygon created message from executor`() {
        val executor = MapStoreExecutor(
            createMapPointUseCase = CreateMapPointUseCase { _: CreateMapPointInput -> null },
            createMapLineUseCase = CreateMapLineUseCase { _: CreateMapLineInput -> null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { input ->
                MapPolygon(
                    id = input.id,
                    vertices = input.vertices,
                    title = input.titleInput,
                    createdAtEpochMillis = input.createdAtEpochMillis,
                )
            },
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "polygon-1" },
            featureSelectionResolver = DefaultMapFeatureSelectionResolver(),
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
            rulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
        )
        val callbacks = TestExecutorCallbacks(
            state = MapStore.State(
                shapeDrawingDraft = MapStore.ShapeDrawingDraft(
                    mode = MapStore.DrawingMode.POLYGON,
                    fixedVertices = listOf(
                        MapVertex(latitude = 55.75, longitude = 37.61),
                        MapVertex(latitude = 55.76, longitude = 37.62),
                        MapVertex(latitude = 55.77, longitude = 37.63),
                    ),
                    titleInput = "Area A",
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(MapStore.Intent.Drawing.Confirmed)

        assertEquals(
            listOf<MapStoreMessage>(
                MapStoreMessage.PolygonCreated(
                    MapPolygon(
                        id = "polygon-1",
                        vertices = listOf(
                            MapVertex(latitude = 55.75, longitude = 37.61),
                            MapVertex(latitude = 55.76, longitude = 37.62),
                            MapVertex(latitude = 55.77, longitude = 37.63),
                        ),
                        title = "Area A",
                        createdAtEpochMillis = 123L,
                    ),
                ),
            ),
            callbacks.messages,
        )
    }

    @Test
    fun `feature click resolves feature and emits shared info window message`() {
        val executor = createExecutor()
        val callbacks = TestExecutorCallbacks(
            state = MapStore.State(
                mapState = MapState(
                    points = listOf(
                        ru.tech.demomapapp.feature.map.api.MapPoint(
                            id = "point-1",
                            latitude = 55.75,
                            longitude = 37.61,
                            title = "Test point",
                            createdAtEpochMillis = 123L,
                        ),
                    ),
                ),
            ),
        )

        executor.init(callbacks)
        executor.executeIntent(
            MapStore.Intent.FeatureSelection.FeatureClicked(
                featureKey = "point-1",
                featureType = MapStore.FeatureType.POINT,
                anchor = MapStore.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
            ),
        )

        assertEquals(
            listOf<MapStoreMessage>(
                MapStoreMessage.FeatureInfoWindowOpened(
                    MapStore.FeatureInfoWindow(
                        title = "Test point",
                        createdAtText = "26.03.2026 10:00",
                        anchor = MapStore.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
                    ),
                ),
            ),
            callbacks.messages,
        )
    }

    @Test
    fun `feature click with missing feature emits no message`() {
        val executor = createExecutor()
        val callbacks = TestExecutorCallbacks(state = MapStore.State())

        executor.init(callbacks)
        executor.executeIntent(
            MapStore.Intent.FeatureSelection.FeatureClicked(
                featureKey = "missing",
                featureType = MapStore.FeatureType.POINT,
                anchor = MapStore.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
            ),
        )

        assertTrue(callbacks.messages.isEmpty())
    }

    private fun createExecutor(): MapStoreExecutor =
        MapStoreExecutor(
            createMapPointUseCase = CreateMapPointUseCase { _: CreateMapPointInput -> null },
            createMapLineUseCase = CreateMapLineUseCase { _: CreateMapLineInput -> null },
            createMapPolygonUseCase = CreateMapPolygonUseCase { _: CreateMapPolygonInput -> null },
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "feature-1" },
            featureSelectionResolver = DefaultMapFeatureSelectionResolver(),
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(
                createdAtFormatter = MapPointCreatedAtFormatter { "26.03.2026 10:00" },
            ),
            rulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
        )

    private class TestExecutorCallbacks(
        override var state: MapStore.State,
    ) : Executor.Callbacks<MapStore.State, MapStoreMessage, Nothing, MapStore.Label> {
        val messages = mutableListOf<MapStoreMessage>()

        override fun onMessage(message: MapStoreMessage) {
            messages += message
        }

        override fun onAction(action: Nothing) = Unit

        override fun onLabel(label: MapStore.Label) = Unit
    }
}
