package ru.tech.demomapapp.feature.map.impl.ruler

internal object RulerReducer {
    fun reduce(state: RulerStore.State, message: RulerStore.Message): RulerStore.State = when (message) {
        is RulerStore.Message.EnabledUpdated -> state.copy(
            isEnabled = message.isEnabled,
            measurement = if (message.isEnabled) state.measurement else null,
            infoWindow = if (message.isEnabled) state.infoWindow else null,
        )

        is RulerStore.Message.LocationStored -> state.copy(currentLocation = message.location)
        is RulerStore.Message.CameraSnapshotStored -> state.copy(lastCameraSnapshot = message.snapshot)
        is RulerStore.Message.MeasurementUpdated -> state.copy(
            measurement = message.measurement,
            infoWindow = message.infoWindow,
        )

        RulerStore.Message.MeasurementCleared -> state.copy(
            measurement = null,
            infoWindow = null,
        )
    }
}
