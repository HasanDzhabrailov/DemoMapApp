package ru.tech.demomapapp.feature.map.impl.viewport

import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal interface ViewportComponent {
    val model: Value<ViewportModel>

    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onZoomInClick()
    fun onZoomOutClick()
    fun onViewportCommandConsumed()
    fun onCenterMarkerClick()
    fun onCenterMarkerMenuDismiss()

    fun interface Output {
        fun onViewportCommandRequested(command: MapViewportCommand)
    }
}
