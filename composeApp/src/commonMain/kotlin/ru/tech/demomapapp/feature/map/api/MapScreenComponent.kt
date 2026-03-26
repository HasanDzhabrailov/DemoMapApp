package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>
    val debugModel: Value<DebugModel>

    fun onCameraIdle(snapshot: MapCameraSnapshot)
    fun onCenterMarkerClick()
    fun onCenterMarkerMenuDismiss()
    fun onCreatePointClick()
    fun onCreatePointLatitudeChange(value: String)
    fun onCreatePointLongitudeChange(value: String)
    fun onCreatePointTitleChange(value: String)
    fun onCreatePointConfirm()
    fun onCreatePointSheetDismiss()

    fun onDebugPanelToggle()

    data class Model(
        val mapState: MapState = MapState(),
        val lastCameraSnapshot: MapCameraSnapshot? = null,
        val isCenterMarkerMenuVisible: Boolean = false,
        val isCreatePointSheetVisible: Boolean = false,
        val createPointDraft: CreatePointDraft? = null,
    )

    data class CreatePointDraft(
        val latitudeInput: String,
        val longitudeInput: String,
        val titleInput: String = "",
    )

    data class DebugModel(
        val isExpanded: Boolean = true,
    )
}
