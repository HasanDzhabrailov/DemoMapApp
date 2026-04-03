package ru.tech.demomapapp.feature.map.ruler

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer

internal class RulerStoreHolder(
    rulerStoreFactory: RulerStoreFactory,
    initialModel: RulerModel,
    inputSource: RulerComponent.InputSource,
) : InstanceKeeper.Instance {
    private val store: RulerStore = rulerStoreFactory.create(initialModel)
    private val mutableModel = MutableValue(store.state.toModel())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel() }))
    private var lastParentState = RulerComponent.ParentState(
        location = initialModel.currentLocation,
        cameraSnapshot = initialModel.lastCameraSnapshot,
    )
    private val parentInputDisposable: Disposable = inputSource.states(::handleParentState)
    private var labelDisposable: Disposable? = null
    private var statesDisposable: Disposable? = null

    val model: Value<RulerModel> = mutableModel

    fun accept(intent: RulerStore.Intent) {
        store.accept(intent)
    }

    fun labels(callback: (RulerStore.Label) -> Unit) {
        labelDisposable?.dispose()
        labelDisposable = store.labels(observer(onNext = callback))
    }

    fun states(callback: (RulerModel) -> Unit) {
        statesDisposable?.dispose()
        statesDisposable = store.states(observer(onNext = { state -> callback(state.toModel()) }))
    }

    private fun handleParentState(state: RulerComponent.ParentState) {
        val previousState = lastParentState
        if (state == previousState) {
            return
        }

        lastParentState = state

        if (state.cameraSnapshot != null && state.cameraSnapshot != previousState.cameraSnapshot) {
            store.accept(RulerStore.Intent.CameraSnapshotReceived(state.cameraSnapshot))
        }

        if (state.location != previousState.location) {
            store.accept(RulerStore.Intent.LocationUpdated(state.location))
        }
    }

    override fun onDestroy() {
        labelDisposable?.dispose()
        statesDisposable?.dispose()
        parentInputDisposable.dispose()
        stateDisposable.dispose()
        store.dispose()
    }
}
