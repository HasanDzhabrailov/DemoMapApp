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
    private val store: MapStore = mapStoreFactory.create(
        initialState = MapStore.State.fromModel(model = initialModel.withoutTransientOutputs()),
    )
    private val mutableModel = MutableValue(store.state.toModel())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))
    private val labelDisposable: Disposable =
        store.labels(observer(onNext = ::handleLabel))

    val model: Value<MapScreenComponent.Model> = mutableModel

    fun accept(intent: MapStore.Intent) {
        store.accept(intent)
    }

    override fun onDestroy() {
        labelDisposable.dispose()
        stateDisposable.dispose()
        store.dispose()
    }

    private fun handleLabel(label: MapStore.Label) {
        when (label) {
            is MapStore.Label.NotificationRequested -> Unit
        }
    }

    private fun MapScreenComponent.Model.withoutTransientOutputs(): MapScreenComponent.Model = copy(
        pendingLocationRequest = null,
    )
}
