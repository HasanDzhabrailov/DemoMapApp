package ru.tech.demomapapp.feature.map.viewport

internal object ViewportReducer {
    fun reduce(state: ViewportStore.State, message: ViewportStore.Message): ViewportStore.State = when (message) {
        is ViewportStore.Message.CameraSnapshotStored -> state.copy(cameraSnapshot = message.snapshot)
        is ViewportStore.Message.PendingCommandUpdated -> state.copy(pendingCommand = message.command)
    }
}
