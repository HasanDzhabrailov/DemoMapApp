package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class MapRouterStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val featureSelectionResolver: MapFeatureSelectionResolver = DefaultMapFeatureSelectionResolver(),
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper =
        DefaultMapFeatureInfoWindowStateMapper(),
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
            executorFactory = {
                MapRouterExecutor(
                    featureSelectionResolver = featureSelectionResolver,
                    featureInfoWindowStateMapper = featureInfoWindowStateMapper,
                )
            },
            reducer = MapRouterReducer(),
        ) {}
}
