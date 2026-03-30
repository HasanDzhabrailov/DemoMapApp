package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) {
    fun create(initialState: MapStore.State = MapStore.State()): MapStore =
        object : MapStore, Store<MapStore.Intent, MapStore.State, MapStore.Label> by storeFactory.create(
            name = "MapStore",
            initialState = initialState,
            bootstrapper = null,
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed interface Message {
        data class StateSynced(val state: MapStore.State) : Message
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
                is MapStore.Intent.SyncState -> callbacks.onMessage(Message.StateSynced(intent.state))
                is MapStore.Intent.CenterMarker,
                is MapStore.Intent.CreatePoint,
                is MapStore.Intent.Drawing,
                is MapStore.Intent.FeatureSelection,
                is MapStore.Intent.Location,
                is MapStore.Intent.Ruler,
                is MapStore.Intent.Tools,
                is MapStore.Intent.Viewport,
                -> Unit
            }
        }

        override fun executeAction(action: Nothing) = Unit

        override fun dispose() = Unit
    }

    private object ReducerImpl : Reducer<MapStore.State, Message> {
        override fun MapStore.State.reduce(msg: Message): MapStore.State =
            when (msg) {
                is Message.StateSynced -> msg.state
            }
    }
}
