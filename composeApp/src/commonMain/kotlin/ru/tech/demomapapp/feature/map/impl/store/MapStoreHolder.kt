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
    private val store: MapStore = mapStoreFactory.create(initialModel = initialModel)
    private val mutableModel = MutableValue(store.state.model)
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.model }))

    val model: Value<MapScreenComponent.Model> = mutableModel

    fun updateModel(model: MapScreenComponent.Model) {
        store.accept(MapStore.Intent.SyncModel(model))
    }

    override fun onDestroy() {
        stateDisposable.dispose()
        store.dispose()
    }
}
