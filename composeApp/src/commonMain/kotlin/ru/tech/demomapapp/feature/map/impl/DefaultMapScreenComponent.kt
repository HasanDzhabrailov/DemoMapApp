package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    private val createMapPointUseCase: CreateMapPointUseCase = DefaultCreateMapPointUseCase(),
    private val createMapLineUseCase: CreateMapLineUseCase = DefaultCreateMapLineUseCase(),
    private val createMapPolygonUseCase: CreateMapPolygonUseCase = DefaultCreateMapPolygonUseCase(),
    private val shapeDrawingDraftUpdater: ShapeDrawingDraftUpdater = DefaultShapeDrawingDraftUpdater(),
    private val timeProvider: TimeProvider = SystemTimeProvider(),
    private val featureIdProvider: FeatureIdProvider = UuidFeatureIdProvider(),
    private val featureSelectionResolver: MapFeatureSelectionResolver = DefaultMapFeatureSelectionResolver(),
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
) : MapScreenComponent, ComponentContext by componentContext {
    private val mutableModel = MutableValue(defaultModel())
    private val mutableDebugModel = MutableValue(defaultDebugModel())

    override val model: Value<MapScreenComponent.Model> = mutableModel
    override val debugModel: Value<MapScreenComponent.DebugModel> = mutableDebugModel

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        mutableModel.value = mutableModel.value.copy(
            lastCameraSnapshot = snapshot,
            selectedFeatureInfoWindow = null,
        )
    }

    override fun onCenterMarkerClick() {
        val model = mutableModel.value
        if (model.drawingMode != null) {
            return
        }
        mutableModel.value = model.copy(
            isCenterMarkerMenuVisible = true,
            selectedFeatureInfoWindow = null,
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
            selectedFeatureInfoWindow = null,
        )
    }

    override fun onCreateLineClick() {
        startDrawing(MapScreenComponent.DrawingMode.LINE)
    }

    override fun onCreatePolygonClick() {
        startDrawing(MapScreenComponent.DrawingMode.POLYGON)
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
                id = featureIdProvider.nextId(),
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

    override fun onDrawingAddPositionClick() {
        val model = mutableModel.value
        val draft = model.shapeDrawingDraft ?: return
        val snapshot = model.lastCameraSnapshot ?: return
        mutableModel.value = model.copy(
            shapeDrawingDraft = shapeDrawingDraftUpdater.addVertex(draft, snapshot),
            selectedFeatureInfoWindow = null,
        )
    }

    override fun onDrawingRemoveLastPositionClick() {
        val model = mutableModel.value
        val draft = model.shapeDrawingDraft ?: return
        mutableModel.value = model.copy(
            shapeDrawingDraft = shapeDrawingDraftUpdater.removeLastVertex(draft),
        )
    }

    override fun onDrawingDetailsClick() {
        val model = mutableModel.value
        val draft = model.shapeDrawingDraft ?: return
        if (!draft.canOpenDetails()) {
            return
        }
        mutableModel.value = model.copy(
            isCreateShapeSheetVisible = true,
        )
    }

    override fun onDrawingDismiss() {
        mutableModel.value = mutableModel.value.copy(
            drawingMode = null,
            shapeDrawingDraft = null,
            isCreateShapeSheetVisible = false,
        )
    }

    override fun onCreateShapeTitleChange(value: String) {
        val model = mutableModel.value
        val draft = model.shapeDrawingDraft ?: return
        mutableModel.value = model.copy(
            shapeDrawingDraft = draft.copy(titleInput = value),
        )
    }

    override fun onCreateShapeConfirm() {
        val model = mutableModel.value
        val draft = model.shapeDrawingDraft ?: return
        val createdAt = timeProvider.currentTimeMillis()
        val id = featureIdProvider.nextId()

        when (draft.mode) {
            MapScreenComponent.DrawingMode.LINE -> {
                val line = createMapLineUseCase.create(
                    CreateMapLineInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                ) ?: return
                mutableModel.value = model.copy(
                    mapState = model.mapState.copy(lines = model.mapState.lines + line),
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                )
            }

            MapScreenComponent.DrawingMode.POLYGON -> {
                val polygon = createMapPolygonUseCase.create(
                    CreateMapPolygonInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                ) ?: return
                mutableModel.value = model.copy(
                    mapState = model.mapState.copy(polygons = model.mapState.polygons + polygon),
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                )
            }
        }
    }

    override fun onCreateShapeSheetDismiss() {
        mutableModel.value = mutableModel.value.copy(
            isCreateShapeSheetVisible = false,
        )
    }

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) {
        val model = mutableModel.value
        val feature = featureSelectionResolver.resolve(model.mapState, featureKey, featureType) ?: return
        mutableModel.value = model.copy(
            isCenterMarkerMenuVisible = false,
            selectedFeatureInfoWindow = featureInfoWindowStateMapper.map(feature, anchor),
        )
    }

    override fun onFeatureInfoWindowDismiss() {
        mutableModel.value = mutableModel.value.copy(
            selectedFeatureInfoWindow = null,
        )
    }

    override fun onDebugPanelToggle() {
        val debugModel = mutableDebugModel.value
        mutableDebugModel.value = debugModel.copy(
            isExpanded = !debugModel.isExpanded,
        )
    }

    private fun startDrawing(mode: MapScreenComponent.DrawingMode) {
        val model = mutableModel.value
        mutableModel.value = model.copy(
            isCenterMarkerMenuVisible = false,
            isCreatePointSheetVisible = false,
            createPointDraft = null,
            drawingMode = mode,
            shapeDrawingDraft = MapScreenComponent.ShapeDrawingDraft(mode = mode),
            isCreateShapeSheetVisible = false,
            selectedFeatureInfoWindow = null,
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

    private fun MapScreenComponent.ShapeDrawingDraft.canOpenDetails(): Boolean =
        fixedVertices.size >= minimumVertexCount()

    private fun MapScreenComponent.ShapeDrawingDraft.minimumVertexCount(): Int =
        when (mode) {
            MapScreenComponent.DrawingMode.LINE -> 2
            MapScreenComponent.DrawingMode.POLYGON -> 3
        }
}

internal fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

internal class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = platformCurrentTimeMillis()
}

internal fun interface FeatureIdProvider {
    fun nextId(): String
}

internal class UuidFeatureIdProvider : FeatureIdProvider {
    override fun nextId(): String = generateMapPointId()
}
