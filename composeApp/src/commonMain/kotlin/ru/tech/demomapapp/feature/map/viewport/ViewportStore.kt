package ru.tech.demomapapp.feature.map.viewport

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal interface ViewportStore : Store<ViewportStore.Intent, ViewportStore.State, ViewportStore.Label> {
    sealed interface Intent {
        data class CameraIdle(val snapshot: MapCameraSnapshot) : Intent
        object ZoomInClicked : Intent
        object ZoomOutClicked : Intent
        object ViewportCommandConsumed : Intent
        object CenterMarkerClicked : Intent
        object CenterMarkerMenuDismissed : Intent
    }

    data class State(
        val cameraSnapshot: MapCameraSnapshot? = null,
        val pendingCommand: MapViewportCommand? = null,
        val isCenterMarkerMenuVisible: Boolean = false,
    ) {
        fun toModel(): ViewportModel = ViewportModel(
            cameraSnapshot = cameraSnapshot,
            pendingCommand = pendingCommand,
            isCenterMarkerMenuVisible = isCenterMarkerMenuVisible,
        )

        companion object {
            fun fromModel(model: ViewportModel): State = State(
                cameraSnapshot = model.cameraSnapshot,
                pendingCommand = model.pendingCommand,
                isCenterMarkerMenuVisible = model.isCenterMarkerMenuVisible,
            )
        }
    }

    sealed interface Message {
        data class CameraSnapshotStored(val snapshot: MapCameraSnapshot) : Message
        data class PendingCommandUpdated(val command: MapViewportCommand?) : Message
        object CenterMarkerMenuOpened : Message
        object CenterMarkerMenuDismissed : Message
    }

    sealed interface Label {
        data class ViewportCommandRequested(val command: MapViewportCommand) : Label
    }
}
