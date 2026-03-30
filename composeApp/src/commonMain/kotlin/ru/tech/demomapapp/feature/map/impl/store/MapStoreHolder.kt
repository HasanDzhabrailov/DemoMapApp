package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal class MapStoreHolder(
    mapStoreFactory: MapStoreFactory,
    initialModel: MapScreenComponent.Model,
) : InstanceKeeper.Instance {
    private val store: MapStore = mapStoreFactory.create(initialState = MapStore.State.fromModel(initialModel))
    private val mutableModel = MutableValue(store.state.toModel())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))

    val model: Value<MapScreenComponent.Model> = mutableModel

    fun accept(intent: MapStore.Intent) {
        store.accept(intent)
    }

    fun updateModel(model: MapScreenComponent.Model) {
        store.accept(MapStore.Intent.SyncState(MapStore.State.fromModel(model)))
    }

    override fun onDestroy() {
        stateDisposable.dispose()
        store.dispose()
    }
}
