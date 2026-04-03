package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultCreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultCreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultCreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.generateMapPointId
import ru.tech.demomapapp.feature.map.impl.platformCurrentTimeMillis

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
