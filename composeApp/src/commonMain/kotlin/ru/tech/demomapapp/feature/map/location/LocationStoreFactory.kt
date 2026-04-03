package ru.tech.demomapapp.feature.map.location

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class LocationStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) {
    fun create(initialModel: LocationModel = LocationModel()): LocationStore {
        return object :
            LocationStore,
            com.arkivanov.mvikotlin.core.store.Store<
                LocationStore.Intent,
                LocationStore.State,
                LocationStore.Label,
                > by storeFactory.create(
                name = "LocationStore",
                initialState = LocationStore.State.fromModel(initialModel),
                executorFactory = ::LocationExecutor,
                reducer = LocationReducer,
            ) {}
    }
}
