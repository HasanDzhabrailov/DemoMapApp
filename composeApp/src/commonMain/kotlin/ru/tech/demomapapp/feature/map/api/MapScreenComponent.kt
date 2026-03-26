package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>
    val debugModel: Value<DebugModel>

    fun onCameraIdle(snapshot: MapCameraSnapshot)

    fun onDebugPanelToggle()

    data class Model(
        val mapState: MapState = MapState(),
    )

    data class DebugModel(
        val isExpanded: Boolean = true,
        val lastCameraSnapshot: MapCameraSnapshot? = null,
    )
}
