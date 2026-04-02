package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class MapRouterStoreFactory(
    private val storeFactory: StoreFactory,
) {

    fun create(): MapRouterStore = object :
        MapRouterStore,
        Store<
            MapRouterStore.Intent,
            MapRouterStore.State,
            MapRouterStore.Label,
            > by storeFactory.create(
            name = "MapRouterStore",
            initialState = MapRouterStore.State(),
            executorFactory = { MapRouterExecutor() },
            reducer = MapRouterReducer(),
        ) {}
}
