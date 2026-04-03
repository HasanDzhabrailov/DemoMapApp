package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal class MapRouterStoreHolder(
    mapRouterStoreFactory: MapRouterStoreFactory,
    initialModel: MapScreenComponent.Model,
) : InstanceKeeper.Instance {
    private val store: MapRouterStore = mapRouterStoreFactory.create()
    private val mutableModel = MutableValue(initialModel)
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))
    private var labelsDisposable: Disposable? = null
    private var statesDisposable: Disposable? = null

    val model: Value<MapScreenComponent.Model> = mutableModel
    val state: MapRouterStore.State
        get() = store.state

    fun accept(intent: MapRouterStore.Intent) {
        store.accept(intent)
    }

    fun labels(callback: (MapRouterStore.Label) -> Unit) {
        labelsDisposable?.dispose()
        labelsDisposable = store.labels(observer(onNext = callback))
    }

    fun states(callback: (MapRouterStore.State) -> Unit): Disposable {
        statesDisposable?.dispose()
        val disposable = store.states(observer(onNext = callback))
        statesDisposable = disposable
        return disposable
    }

    override fun onDestroy() {
        labelsDisposable?.dispose()
        statesDisposable?.dispose()
        stateDisposable.dispose()
        store.dispose()
    }
}
