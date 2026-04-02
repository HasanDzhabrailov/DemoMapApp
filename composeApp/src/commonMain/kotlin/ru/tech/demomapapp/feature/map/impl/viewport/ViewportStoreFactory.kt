package ru.tech.demomapapp.feature.map.impl.viewport

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class ViewportStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) {
    fun create(): ViewportStore {
        return object :
            ViewportStore,
            Store<ViewportStore.Intent, ViewportStore.State, ViewportStore.Label> by storeFactory.create(
                name = "ViewportStore",
                initialState = ViewportStore.State(),
                executorFactory = { ViewportExecutor() },
                reducer = ViewportReducer::reduce,
            ) {}
    }
}
