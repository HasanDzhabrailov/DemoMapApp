package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal class MapStoreHolder(
    mapStoreFactory: MapStoreFactory,
    initialModel: MapScreenComponent.Model,
) : InstanceKeeper.Instance {
    private val store: MapStore = mapStoreFactory.create(
        initialState = MapStore.State.fromModel(
            model = initialModel.withoutTransientOutputs(),
            activeLocationRequest = initialModel.pendingLocationRequest,
        ),
    )
    private var pendingLocationRequest: MapLocationRequest? = initialModel.pendingLocationRequest
    private var pendingViewportCommand: MapViewportCommand? = initialModel.pendingViewportCommand
    private val mutableModel = MutableValue(store.state.toModel().withTransientOutputs())
    private val stateDisposable: Disposable =
        store.states(observer(onNext = { state -> mutableModel.value = state.toModel().withTransientOutputs() }))
    private val labelDisposable: Disposable =
        store.labels(observer(onNext = ::handleLabel))

    val model: Value<MapScreenComponent.Model> = mutableModel

    fun accept(intent: MapStore.Intent) {
        store.accept(intent)
    }

    fun updateModel(model: MapScreenComponent.Model) {
        pendingLocationRequest = model.pendingLocationRequest
        pendingViewportCommand = model.pendingViewportCommand
        store.accept(
            MapStore.Intent.SyncState(
                MapStore.State.fromModel(
                    model = model.withoutTransientOutputs(),
                    activeLocationRequest = store.state.activeLocationRequest,
                ),
            ),
        )
    }

    fun consumeLocationRequest() {
        pendingLocationRequest = null
        mutableModel.value = mutableModel.value.copy(pendingLocationRequest = null)
        store.accept(MapStore.Intent.Location.LocationRequestConsumed)
    }

    fun consumeViewportCommand() {
        pendingViewportCommand = null
        mutableModel.value = mutableModel.value.copy(pendingViewportCommand = null)
        store.accept(MapStore.Intent.Viewport.ViewportCommandConsumed)
    }

    override fun onDestroy() {
        labelDisposable.dispose()
        stateDisposable.dispose()
        store.dispose()
    }

    private fun handleLabel(label: MapStore.Label) {
        when (label) {
            is MapStore.Label.Location.RequestIssued -> {
                pendingLocationRequest = label.request
                mutableModel.value = mutableModel.value.copy(pendingLocationRequest = label.request)
            }

            is MapStore.Label.Viewport.CommandRequested -> {
                pendingViewportCommand = label.command
                mutableModel.value = mutableModel.value.copy(pendingViewportCommand = label.command)
            }

            is MapStore.Label.NotificationRequested -> Unit
        }
    }

    private fun MapScreenComponent.Model.withTransientOutputs(): MapScreenComponent.Model =
        copy(
            pendingLocationRequest = pendingLocationRequest,
            pendingViewportCommand = pendingViewportCommand,
        )

    private fun MapScreenComponent.Model.withoutTransientOutputs(): MapScreenComponent.Model =
        copy(
            pendingLocationRequest = null,
            pendingViewportCommand = null,
        )
}
