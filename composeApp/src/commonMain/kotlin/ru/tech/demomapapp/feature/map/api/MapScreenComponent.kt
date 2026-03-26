package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>
    val debugModel: Value<DebugModel>

    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onCenterMarkerClick()

    fun onDebugPanelToggle()

    data class Model(
        val mapState: MapState = MapState(),
        val lastCameraSnapshot: MapCameraSnapshot? = null,
    )

    data class DebugModel(
        val isExpanded: Boolean = true,
    )
}
