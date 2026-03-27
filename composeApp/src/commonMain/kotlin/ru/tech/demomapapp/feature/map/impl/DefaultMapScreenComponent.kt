package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

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

    override val model: Value<MapScreenComponent.Model> = mutableModel

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        mutableModel.value = mutableModel.value.copy(
            lastCameraSnapshot = snapshot,
            selectedFeatureInfoWindow = null,
        )
    }

    override fun onMapToolsClick() {
        val model = mutableModel.value
        val isMenuVisible = !model.isMapToolsMenuVisible
        mutableModel.value = model.copy(
            isMapToolsMenuVisible = isMenuVisible,
            isCenterMarkerMenuVisible = if (isMenuVisible) {
                false
            } else {
                model.isCenterMarkerMenuVisible
            },
            selectedFeatureInfoWindow = if (isMenuVisible) {
                null
            } else {
                model.selectedFeatureInfoWindow
            },
        )
    }

    override fun onMapToolsDismiss() {
        mutableModel.value = mutableModel.value.copy(
            isMapToolsMenuVisible = false,
        )
    }

    override fun onZoomInClick() {
        mutableModel.value = mutableModel.value.copy(
            pendingViewportCommand = MapViewportCommand.ZoomIn,
        )
    }

    override fun onZoomOutClick() {
        mutableModel.value = mutableModel.value.copy(
            pendingViewportCommand = MapViewportCommand.ZoomOut,
        )
    }

    override fun onAvailableMapsClick() {
        mutableModel.value = mutableModel.value.copy(
            isMapToolsMenuVisible = false,
        )
    }

    override fun onMapsOnScreenClick() {
        mutableModel.value = mutableModel.value.copy(
            isMapToolsMenuVisible = false,
        )
    }

    override fun onGpsToggle() {
        val model = mutableModel.value
        if (model.pendingLocationRequest == MapLocationRequest.EnableGpsLocationRequest) {
            mutableModel.value = model.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                pendingLocationRequest = null,
            )
            return
        }

        mutableModel.value = when (model.myLocationMode) {
            MyLocationMode.GPS -> model.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                pendingLocationRequest = null,
            )

            MyLocationMode.OFF,
            MyLocationMode.MANUAL_PLACEHOLDER,
            -> model.copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                pendingLocationRequest = MapLocationRequest.EnableGpsLocationRequest,
            )
        }
    }

    override fun onMyLocationClick() {
        val model = mutableModel.value
        mutableModel.value = when (model.myLocationMode) {
            MyLocationMode.GPS -> model

            MyLocationMode.OFF,
            MyLocationMode.MANUAL_PLACEHOLDER,
            -> {
                val snapshot = model.lastCameraSnapshot ?: return
                model.copy(
                    myLocationMode = MyLocationMode.MANUAL_PLACEHOLDER,
                    currentLocationMarker = MapLocationMarker(
                        latitude = snapshot.latitude,
                        longitude = snapshot.longitude,
                        isPlaceholder = true,
                    ),
                    pendingLocationRequest = null,
                )
            }
        }
    }

    override fun onCurrentLocationFocusClick() {
        val model = mutableModel.value
        val marker = model.currentLocationMarker
        mutableModel.value = when {
            marker != null -> {
                model.copy(
                    pendingViewportCommand = MapViewportCommand.MoveTo(
                        latitude = marker.latitude,
                        longitude = marker.longitude,
                    ),
                )
            }

            model.myLocationMode == MyLocationMode.GPS -> {
                model.copy(
                    pendingLocationRequest = MapLocationRequest.RecenterToGpsLocationRequest,
                )
            }

            else -> model
        }
    }

    override fun onLocationRequestConsumed() {
        mutableModel.value = mutableModel.value.copy(
            pendingLocationRequest = null,
        )
    }

    override fun onLocationResult(result: LocationRequestResult) {
        val model = mutableModel.value
        val request = model.pendingLocationRequest
        mutableModel.value = when (result) {
            LocationRequestResult.PermissionDenied -> {
                model.copy(
                    myLocationMode = MyLocationMode.OFF,
                    currentLocationMarker = null,
                    pendingLocationRequest = null,
                )
            }

            LocationRequestResult.LocationUnavailable -> {
                if (model.myLocationMode == MyLocationMode.GPS && request != MapLocationRequest.EnableGpsLocationRequest) {
                    model.copy(
                        pendingLocationRequest = null,
                    )
                } else {
                    model.copy(
                        myLocationMode = MyLocationMode.OFF,
                        currentLocationMarker = null,
                        pendingLocationRequest = null,
                    )
                }
            }

            is LocationRequestResult.LocationResolved -> model.copy(
                myLocationMode = MyLocationMode.GPS,
                currentLocationMarker = MapLocationMarker(
                    latitude = result.latitude,
                    longitude = result.longitude,
                    isPlaceholder = false,
                ),
                pendingLocationRequest = null,
                pendingViewportCommand = MapViewportCommand.MoveTo(
                    latitude = result.latitude,
                    longitude = result.longitude,
                ),
            )
        }
    }

    override fun onRulerToggle() {
        val model = mutableModel.value
        mutableModel.value = model.copy(
            isRulerEnabled = !model.isRulerEnabled,
        )
    }

    override fun onViewportCommandConsumed() {
        mutableModel.value = mutableModel.value.copy(
            pendingViewportCommand = null,
        )
    }

    override fun onCenterMarkerClick() {
        val model = mutableModel.value
        if (model.drawingMode != null) {
            return
        }
        mutableModel.value = model.copy(
            isMapToolsMenuVisible = false,
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
            isMapToolsMenuVisible = false,
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
            isMapToolsMenuVisible = false,
            isCenterMarkerMenuVisible = false,
            selectedFeatureInfoWindow = featureInfoWindowStateMapper.map(feature, anchor),
        )
    }

    override fun onFeatureInfoWindowDismiss() {
        mutableModel.value = mutableModel.value.copy(
            selectedFeatureInfoWindow = null,
        )
    }

    private fun startDrawing(mode: MapScreenComponent.DrawingMode) {
        val model = mutableModel.value
        mutableModel.value = model.copy(
            isMapToolsMenuVisible = false,
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
