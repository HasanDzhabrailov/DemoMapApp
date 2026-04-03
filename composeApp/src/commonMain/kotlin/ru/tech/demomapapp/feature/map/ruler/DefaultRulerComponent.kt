package ru.tech.demomapapp.feature.map.ruler

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker

internal class DefaultRulerComponent(
    componentContext: ComponentContext,
    private val rulerStoreFactory: RulerStoreFactory,
    initialModel: RulerModel = RulerModel(),
    private val output: RulerComponent.Output,
) : RulerComponent, ComponentContext by componentContext {

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        RulerStoreHolder(rulerStoreFactory, initialModel)
    }
    private val states = holder.states { output.onStateChanged() }

    override val model: Value<RulerModel> = holder.model

    override fun onToggleClicked() = holder.accept(RulerStore.Intent.ToggleClicked)

    override fun onLocationUpdated(location: MapLocationMarker?) =
        holder.accept(RulerStore.Intent.LocationUpdated(location))

    override fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot) =
        holder.accept(RulerStore.Intent.CameraSnapshotReceived(snapshot))

    init {
        holder.labels { label ->
            when (label) {
                is RulerStore.Label.ViewportCommandRequested -> output.onViewportCommandRequested(label.command)
            }
        }
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultRulerComponent.rulerStoreHolder"
    }
}
