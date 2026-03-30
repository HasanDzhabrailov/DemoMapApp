package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) {
    fun create(initialModel: MapScreenComponent.Model = MapScreenComponent.Model()): MapStore =
        object : MapStore, Store<MapStore.Intent, MapStore.State, MapStore.Label> by storeFactory.create(
            name = "MapStore",
            initialState = MapStore.State(model = initialModel),
            bootstrapper = null,
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed interface Message {
        data class ModelSynced(val model: MapScreenComponent.Model) : Message
    }

    private class ExecutorImpl :
        com.arkivanov.mvikotlin.core.store.Executor<MapStore.Intent, Nothing, MapStore.State, Message, MapStore.Label> {

        private lateinit var callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
            MapStore.State,
            Message,
            Nothing,
            MapStore.Label,
        >

        override fun init(
            callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
                MapStore.State,
                Message,
                Nothing,
                MapStore.Label,
            >,
        ) {
            this.callbacks = callbacks
        }

        override fun executeIntent(intent: MapStore.Intent) {
            when (intent) {
                is MapStore.Intent.SyncModel -> callbacks.onMessage(Message.ModelSynced(intent.model))
            }
        }

        override fun executeAction(action: Nothing) = Unit

        override fun dispose() = Unit
    }

    private object ReducerImpl : Reducer<MapStore.State, Message> {
        override fun MapStore.State.reduce(msg: Message): MapStore.State =
            when (msg) {
                is Message.ModelSynced -> copy(model = msg.model)
            }
    }
}
