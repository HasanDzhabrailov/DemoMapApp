package ru.tech.demomapapp.feature.map.ruler

import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.rx.Disposable
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.RulerUiContract

/**
 * RulerComponent extends RulerUiContract to expose minimal UI interface.
 * Internal methods remain in this interface only.
 */
interface RulerComponent : RulerUiContract {
    override val model: Value<RulerModel>

    fun onToggleClicked()
    fun onLocationUpdated(location: MapLocationMarker?)
    fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot)

    data class ParentState(
        val location: MapLocationMarker?,
        val cameraSnapshot: MapCameraSnapshot?,
    )

    fun interface InputSource {
        fun states(callback: (ParentState) -> Unit): Disposable
    }

    interface Output {
        fun onStateChanged()
        fun onViewportCommandRequested(command: MapViewportCommand)
    }
}
