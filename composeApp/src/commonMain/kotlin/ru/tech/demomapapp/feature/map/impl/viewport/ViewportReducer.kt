package ru.tech.demomapapp.feature.map.impl.viewport

internal object ViewportReducer {
    fun reduce(state: ViewportStore.State, message: ViewportStore.Message): ViewportStore.State = when (message) {
        is ViewportStore.Message.CameraSnapshotStored -> state.copy(cameraSnapshot = message.snapshot)
        is ViewportStore.Message.PendingCommandUpdated -> state.copy(pendingCommand = message.command)
        ViewportStore.Message.CenterMarkerMenuOpened -> state.copy(isCenterMarkerMenuVisible = true)
        ViewportStore.Message.CenterMarkerMenuDismissed -> state.copy(isCenterMarkerMenuVisible = false)
    }
}
