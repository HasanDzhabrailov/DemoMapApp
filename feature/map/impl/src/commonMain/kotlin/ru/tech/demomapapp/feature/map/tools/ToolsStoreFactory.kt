package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class ToolsStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) {
    fun create(initialState: ToolsStore.State = ToolsStore.State()): ToolsStore = object :
        ToolsStore,
        Store<ToolsStore.Intent, ToolsStore.State, ToolsStore.Label> by storeFactory.create(
            name = "ToolsStore",
            initialState = initialState,
            executorFactory = { ToolsExecutor() },
            reducer = ToolsReducer::reduce,
        ) {}
}
