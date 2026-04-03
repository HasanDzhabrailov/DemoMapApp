package ru.tech.demomapapp.feature.map.impl.ruler

import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

interface RulerComponent {
    val model: Value<RulerModel>

    fun onToggleClicked()
    fun onLocationUpdated(location: MapLocationMarker?)
    fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot)

    interface Output {
        fun onStateChanged()
        fun onViewportCommandRequested(command: MapViewportCommand)
    }
}
