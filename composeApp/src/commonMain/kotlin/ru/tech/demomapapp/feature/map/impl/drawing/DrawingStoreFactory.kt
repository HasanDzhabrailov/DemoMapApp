package ru.tech.demomapapp.feature.map.impl.drawing

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase

internal class DrawingStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val createMapPointUseCase: CreateMapPointUseCase,
    private val createMapLineUseCase: CreateMapLineUseCase,
    private val createMapPolygonUseCase: CreateMapPolygonUseCase,
    private val timeProvider: () -> Long,
    private val featureIdProvider: () -> String,
) {
    fun create(): DrawingStore {
        return object :
            DrawingStore,
            com.arkivanov.mvikotlin.core.store.Store<
                DrawingStore.Intent,
                DrawingStore.State,
                DrawingStore.Label,
                > by storeFactory.create(
                name = "DrawingStore",
                initialState = DrawingStore.State(),
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
