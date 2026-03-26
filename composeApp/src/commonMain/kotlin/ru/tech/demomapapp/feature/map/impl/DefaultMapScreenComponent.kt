package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    private val createMapPointUseCase: CreateMapPointUseCase = DefaultCreateMapPointUseCase(),
    private val timeProvider: TimeProvider = SystemTimeProvider(),
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
            isCreatePointSheetVisible = model.lastCameraSnapshot != null,
            createPointDraft = model.lastCameraSnapshot?.toCreatePointDraft(),
        )
    }

    override fun onCreatePointLatitudeChange(value: String) {
        updateCreatePointDraft { copy(latitudeInput = value) }
    }

    override fun onCreatePointLongitudeChange(value: String) {
        updateCreatePointDraft { copy(longitudeInput = value) }
    }

    override fun onCreatePointTitleChange(value: String) {
        updateCreatePointDraft { copy(titleInput = value) }
    }

    override fun onCreatePointConfirm() {
        val model = mutableModel.value
        val draft = model.createPointDraft ?: return
        val point = createMapPointUseCase.create(
            CreateMapPointInput(
                latitudeInput = draft.latitudeInput,
                longitudeInput = draft.longitudeInput,
                titleInput = draft.titleInput,
                createdAtEpochMillis = timeProvider.currentTimeMillis(),
            ),
        ) ?: return

        mutableModel.value = model.copy(
            mapState = model.mapState.copy(
                points = model.mapState.points + point,
            ),
            isCreatePointSheetVisible = false,
            createPointDraft = null,
        )
    }

    override fun onCreatePointSheetDismiss() {
        mutableModel.value = mutableModel.value.copy(
            isCreatePointSheetVisible = false,
            createPointDraft = null,
        )
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

    private fun updateCreatePointDraft(
        transform: MapScreenComponent.CreatePointDraft.() -> MapScreenComponent.CreatePointDraft,
    ) {
        val model = mutableModel.value
        val draft = model.createPointDraft ?: return
        mutableModel.value = model.copy(
            createPointDraft = draft.transform(),
        )
    }

    private fun MapCameraSnapshot.toCreatePointDraft(): MapScreenComponent.CreatePointDraft =
        MapScreenComponent.CreatePointDraft(
            latitudeInput = latitude.toString(),
            longitudeInput = longitude.toString(),
        )
}

internal fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

internal class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = platformCurrentTimeMillis()
}
