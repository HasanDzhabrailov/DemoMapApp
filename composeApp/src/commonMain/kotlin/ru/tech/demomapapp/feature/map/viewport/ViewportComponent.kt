package ru.tech.demomapapp.feature.map.viewport

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal interface ViewportComponent {
    val model: Value<ViewportModel>
    val childSlot: Value<ChildSlot<*, Child>>

    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onZoomInClick()
    fun onZoomOutClick()
    fun onViewportCommandConsumed()
    fun onCenterMarkerClick()
    fun onCenterMarkerMenuDismiss()

    sealed interface Child {
        data object Menu : Child
    }

    interface Output {
        fun onStateChanged()
        fun onViewportCommandRequested(command: MapViewportCommand)
    }
}
