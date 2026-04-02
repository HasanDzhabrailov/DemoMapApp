package ru.tech.demomapapp.feature.map.impl.ruler

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer

internal class RulerStoreHolder(
    rulerStoreFactory: RulerStoreFactory,
) : InstanceKeeper.Instance {
    private val store: RulerStore = rulerStoreFactory.create()
    private val mutableModel = MutableValue(store.state.toModel())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))
    private var labelDisposable: Disposable? = null

    val model: Value<RulerModel> = mutableModel

    fun accept(intent: RulerStore.Intent) {
        store.accept(intent)
    }

    fun labels(callback: (RulerStore.Label) -> Unit) {
        labelDisposable?.dispose()
        labelDisposable = store.labels(observer(onNext = callback))
    }

    override fun onDestroy() {
        labelDisposable?.dispose()
        stateDisposable.dispose()
        store.dispose()
    }
}
