package ru.tech.demomapapp.feature.map.viewport

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.ViewportUiContract

/**
 * ViewportComponent extends ViewportUiContract to expose minimal UI interface.
 * Internal methods (onCameraIdle, onCenterMarkerClick) remain in this interface only.
 */
interface ViewportComponent : ViewportUiContract {
    override val model: Value<ViewportModel>
    override val childSlot: Value<ChildSlot<*, Child>>

    fun onCameraIdle(snapshot: MapCameraSnapshot)
    override fun onZoomInClick()
    override fun onZoomOutClick()
    override fun onViewportCommandConsumed()
    fun onCenterMarkerClick()
    override fun onCenterMarkerMenuDismiss()

    sealed interface Child : ViewportUiContract.Child {
        data object Menu : Child
    }

    interface Output {
        fun onStateChanged()
        fun onViewportCommandRequested(command: MapViewportCommand)
    }
}
