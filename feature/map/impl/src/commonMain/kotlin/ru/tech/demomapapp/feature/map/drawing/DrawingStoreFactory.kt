package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.DefaultCreateMapLineUseCase
import ru.tech.demomapapp.feature.map.DefaultCreateMapPointUseCase
import ru.tech.demomapapp.feature.map.DefaultCreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.generateMapPointId
import ru.tech.demomapapp.feature.map.platformCurrentTimeMillis

internal class DrawingStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val createMapPointUseCase: CreateMapPointUseCase = DefaultCreateMapPointUseCase(),
    private val createMapLineUseCase: CreateMapLineUseCase = DefaultCreateMapLineUseCase(),
    private val createMapPolygonUseCase: CreateMapPolygonUseCase = DefaultCreateMapPolygonUseCase(),
    private val timeProvider: () -> Long = ::platformCurrentTimeMillis,
    private val featureIdProvider: () -> String = ::generateMapPointId,
) {
    fun create(initialModel: DrawingModel = DrawingModel()): DrawingStore {
        return object :
            DrawingStore,
            com.arkivanov.mvikotlin.core.store.Store<
                DrawingStore.Intent,
                DrawingStore.State,
                DrawingStore.Label,
                > by storeFactory.create(
                name = "DrawingStore",
                initialState = DrawingStore.State.fromModel(initialModel),
                executorFactory = {
                    DrawingExecutor(
                        createMapPointUseCase = createMapPointUseCase,
                        createMapLineUseCase = createMapLineUseCase,
                        createMapPolygonUseCase = createMapPolygonUseCase,
                        timeProvider = timeProvider,
                        featureIdProvider = featureIdProvider,
                    )
                },
                reducer = DrawingReducer::reduce,
            ) {}
    }
}
