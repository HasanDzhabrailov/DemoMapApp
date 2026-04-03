package ru.tech.demomapapp.feature.map.impl.tools

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer

internal class ToolsStoreHolder(
    toolsStoreFactory: ToolsStoreFactory,
    initialModel: ToolsModel,
) : InstanceKeeper.Instance {
    private val store: ToolsStore = toolsStoreFactory.create(
        initialState = ToolsStore.State.fromModel(initialModel),
    )
    private val mutableModel = MutableValue(store.state.toModel())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))
    private var statesDisposable: Disposable? = null

    val model: Value<ToolsModel> = mutableModel

    fun accept(intent: ToolsStore.Intent) {
        store.accept(intent)
    }

    fun labels(onLabel: (ToolsStore.Label) -> Unit): Disposable = store.labels(observer(onNext = onLabel))

    fun states(onState: (ToolsModel) -> Unit): Disposable {
        statesDisposable?.dispose()
        return store.states(observer(onNext = { state -> onState(state.toModel()) })).also {
            statesDisposable = it
        }
    }

    override fun onDestroy() {
        statesDisposable?.dispose()
        stateDisposable.dispose()
        store.dispose()
    }
}
