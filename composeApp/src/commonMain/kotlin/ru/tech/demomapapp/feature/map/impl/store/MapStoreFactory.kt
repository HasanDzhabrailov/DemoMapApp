package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.impl.DefaultRulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.DefaultRulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
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
                    rulerMeasurementCalculator = rulerMeasurementCalculator,
                    rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
                )
            },
            reducer = MapStoreReducer,
        ) {}
}
