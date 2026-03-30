package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultRulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.DefaultRulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.TimeProvider

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val createMapPointUseCase: CreateMapPointUseCase,
    private val timeProvider: TimeProvider,
    private val featureIdProvider: FeatureIdProvider,
    private val rulerMeasurementCalculator: RulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
) {
    fun create(initialState: MapStore.State = MapStore.State()): MapStore =
        object : MapStore, Store<MapStore.Intent, MapStore.State, MapStore.Label> by storeFactory.create(
            name = "MapStore",
            initialState = initialState,
            bootstrapper = null,
            executorFactory = {
                MapStoreExecutor(
                    createMapPointUseCase = createMapPointUseCase,
                    timeProvider = timeProvider,
                    featureIdProvider = featureIdProvider,
                    rulerMeasurementCalculator = rulerMeasurementCalculator,
                    rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
                )
            },
            reducer = MapStoreReducer,
        ) {}
}
