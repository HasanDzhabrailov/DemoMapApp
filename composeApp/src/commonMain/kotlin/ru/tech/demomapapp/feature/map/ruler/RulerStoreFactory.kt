package ru.tech.demomapapp.feature.map.ruler

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class RulerStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val rulerMeasurementCalculator: RulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
) {
    fun create(initialModel: RulerModel = RulerModel()): RulerStore {
        return object :
            RulerStore,
            com.arkivanov.mvikotlin.core.store.Store<
                RulerStore.Intent,
                RulerStore.State,
                RulerStore.Label,
                > by storeFactory.create(
                name = "RulerStore",
                initialState = RulerStore.State.fromModel(initialModel),
                executorFactory = {
                    RulerExecutor(
                        rulerMeasurementCalculator = rulerMeasurementCalculator,
                        rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
                    )
                },
                reducer = RulerReducer::reduce,
            ) {}
    }
}
