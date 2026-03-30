package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointInput
import ru.tech.demomapapp.feature.map.impl.DefaultRulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.DefaultRulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
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
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "point-1" },
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
            timeProvider = TimeProvider { 123L },
            featureIdProvider = FeatureIdProvider { "point-1" },
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
