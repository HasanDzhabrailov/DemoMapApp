package ru.tech.demomapapp.feature.map.location

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer

internal class LocationStoreHolder(
    locationStoreFactory: LocationStoreFactory,
    initialModel: LocationModel,
) : InstanceKeeper.Instance {
    private val store: LocationStore = locationStoreFactory.create(initialModel)
    private val mutableModel = MutableValue(store.state.toModel())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))
    private var labelDisposable: Disposable? = null
    private var statesDisposable: Disposable? = null

    val model: Value<LocationModel> = mutableModel

    fun accept(intent: LocationStore.Intent) {
        store.accept(intent)
    }

    fun labels(callback: (LocationStore.Label) -> Unit) {
        labelDisposable?.dispose()
        labelDisposable = store.labels(observer(onNext = callback))
    }

    fun states(callback: (LocationModel) -> Unit) {
        statesDisposable?.dispose()
        statesDisposable = store.states(observer(onNext = { state -> callback(state.toModel()) }))
    }

    override fun onDestroy() {
        labelDisposable?.dispose()
        statesDisposable?.dispose()
        stateDisposable.dispose()
        store.dispose()
    }
}
