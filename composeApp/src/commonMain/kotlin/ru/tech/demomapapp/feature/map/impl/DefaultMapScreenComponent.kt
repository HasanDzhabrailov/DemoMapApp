package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    private val onCreatePointRequested: (MapCameraSnapshot) -> Unit = {},
) : MapScreenComponent, ComponentContext by componentContext {

    private val mutableModel = MutableValue(defaultModel())
    private val mutableDebugModel = MutableValue(defaultDebugModel())

    override val model: Value<MapScreenComponent.Model> = mutableModel
    override val debugModel: Value<MapScreenComponent.DebugModel> = mutableDebugModel

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        mutableModel.value = mutableModel.value.copy(
            lastCameraSnapshot = snapshot,
        )
    }

    override fun onCenterMarkerClick() {
        mutableModel.value = mutableModel.value.copy(
            isCenterMarkerMenuVisible = true,
        )
    }

    override fun onCenterMarkerMenuDismiss() {
        mutableModel.value = mutableModel.value.copy(
            isCenterMarkerMenuVisible = false,
        )
    }

    override fun onCreatePointClick() {
        val model = mutableModel.value
        mutableModel.value = model.copy(
            isCenterMarkerMenuVisible = false,
        )
        model.lastCameraSnapshot?.let(onCreatePointRequested)
    }

    override fun onDebugPanelToggle() {
        val debugModel = mutableDebugModel.value
        mutableDebugModel.value = debugModel.copy(
            isExpanded = !debugModel.isExpanded,
        )
    }

    private fun defaultModel(): MapScreenComponent.Model =
        MapScreenComponent.Model()

    private fun defaultDebugModel(): MapScreenComponent.DebugModel =
        MapScreenComponent.DebugModel()
}
