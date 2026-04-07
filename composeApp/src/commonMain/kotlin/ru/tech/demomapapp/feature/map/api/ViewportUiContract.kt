package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

/**
 * UI contract for viewport controls.
 * Minimal interface exposing only what the UI needs.
 */
interface ViewportUiContract {
    val model: Value<ViewportModel>
    val childSlot: Value<ChildSlot<*, Child>>

    fun onZoomInClick()
    fun onZoomOutClick()
    fun onViewportCommandConsumed()
    fun onCenterMarkerMenuDismiss()

    interface Child {
        data object Menu : Child
    }
}

/**
 * Model for viewport UI state.
 * Defined in API to avoid internal imports.
 */
data class ViewportModel(
    val cameraSnapshot: MapCameraSnapshot? = null,
    val pendingCommand: MapViewportCommand? = null,
    val isCenterMarkerMenuVisible: Boolean = false,
)