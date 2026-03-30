package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.impl.store.MapStoreFactory
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreHolder
import ru.tech.demomapapp.feature.map.impl.store.toStoreAnchor
import ru.tech.demomapapp.feature.map.impl.store.toStoreFeatureType

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
    private val rulerMeasurementCalculator: RulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
    private val mapStoreFactory: MapStoreFactory = MapStoreFactory(),
) : MapScreenComponent, ComponentContext by componentContext {
    private val mapStoreHolder = instanceKeeper.getOrCreate(key = MAP_STORE_HOLDER_KEY) {
        MapStoreHolder(
            mapStoreFactory = mapStoreFactory,
            initialModel = defaultModel(),
        )
    }
    override val model: Value<MapScreenComponent.Model> = mapStoreHolder.model

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        acceptIntent(MapStore.Intent.Viewport.CameraIdle(snapshot))
        setModel(recalculateRulerState(
            currentModel().copy(
                lastCameraSnapshot = snapshot,
                selectedFeatureInfoWindow = null,
            ),
        ))
    }

    override fun onMapToolsClick() {
        acceptIntent(MapStore.Intent.Tools.MapToolsClicked)
        val model = currentModel()
        val isMenuVisible = !model.isMapToolsMenuVisible
        setModel(model.copy(
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
        ))
    }

    override fun onMapToolsDismiss() {
        acceptIntent(MapStore.Intent.Tools.MapToolsDismissed)
        setModel(currentModel().copy(
            isMapToolsMenuVisible = false,
        ))
    }

    override fun onZoomInClick() {
        acceptIntent(MapStore.Intent.Viewport.ZoomInClicked)
        setModel(currentModel().copy(
            pendingViewportCommand = MapViewportCommand.ZoomIn,
        ))
    }

    override fun onZoomOutClick() {
        acceptIntent(MapStore.Intent.Viewport.ZoomOutClicked)
        setModel(currentModel().copy(
            pendingViewportCommand = MapViewportCommand.ZoomOut,
        ))
    }

    override fun onAvailableMapsClick() {
        acceptIntent(MapStore.Intent.Tools.AvailableMapsClicked)
        setModel(currentModel().copy(
            isMapToolsMenuVisible = false,
        ))
    }

    override fun onMapsOnScreenClick() {
        acceptIntent(MapStore.Intent.Tools.MapsOnScreenClicked)
        setModel(currentModel().copy(
            isMapToolsMenuVisible = false,
        ))
    }

    override fun onGpsToggle() {
        acceptIntent(MapStore.Intent.Location.GpsToggled)
        val model = currentModel()
        if (model.pendingLocationRequest == MapLocationRequest.EnableGpsLocationRequest) {
            setModel(recalculateRulerState(
                model.copy(
                    myLocationMode = MyLocationMode.OFF,
                    currentLocationMarker = null,
                    pendingLocationRequest = null,
                ),
            ))
            return
        }

        setModel(recalculateRulerState(
            when (model.myLocationMode) {
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
            },
        ))
    }

    override fun onMyLocationClick() {
        acceptIntent(MapStore.Intent.Location.MyLocationClicked)
        val model = currentModel()
        setModel(recalculateRulerState(
            when (model.myLocationMode) {
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
            },
        ))
    }

    override fun onCurrentLocationFocusClick() {
        acceptIntent(MapStore.Intent.Location.CurrentLocationFocusClicked)
        val model = currentModel()
        val marker = model.currentLocationMarker
        setModel(when {
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
        })
    }

    override fun onLocationRequestConsumed() {
        acceptIntent(MapStore.Intent.Location.LocationRequestConsumed)
        setModel(currentModel().copy(
            pendingLocationRequest = null,
        ))
    }

    override fun onLocationResult(result: LocationRequestResult) {
        acceptIntent(MapStore.Intent.Location.LocationResultReceived(result))
        val model = currentModel()
        val request = model.pendingLocationRequest
        setModel(recalculateRulerState(
            when (result) {
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
            },
        ))
    }

    override fun onRulerToggle() {
        acceptIntent(MapStore.Intent.Ruler.Toggled)
        val model = currentModel()
        setModel(if (model.isRulerEnabled) {
            clearRulerState(
                model.copy(isRulerEnabled = false),
            )
        } else {
            recalculateRulerState(
                model.copy(isRulerEnabled = true),
            )
        })
    }

    override fun onViewportCommandConsumed() {
        acceptIntent(MapStore.Intent.Viewport.ViewportCommandConsumed)
        setModel(currentModel().copy(
            pendingViewportCommand = null,
        ))
    }

    override fun onCenterMarkerClick() {
        acceptIntent(MapStore.Intent.CenterMarker.Clicked)
        val model = currentModel()
        if (model.drawingMode != null) {
            return
        }
        setModel(model.copy(
            isMapToolsMenuVisible = false,
            isCenterMarkerMenuVisible = true,
            selectedFeatureInfoWindow = null,
        ))
    }

    override fun onCenterMarkerMenuDismiss() {
        acceptIntent(MapStore.Intent.CenterMarker.MenuDismissed)
        setModel(currentModel().copy(
            isCenterMarkerMenuVisible = false,
        ))
    }

    override fun onCreatePointClick() {
        acceptIntent(MapStore.Intent.CreatePoint.Clicked)
        val model = currentModel()
        setModel(model.copy(
            isMapToolsMenuVisible = false,
            isCenterMarkerMenuVisible = false,
            isCreatePointSheetVisible = model.lastCameraSnapshot != null,
            createPointDraft = model.lastCameraSnapshot?.toCreatePointDraft(),
            selectedFeatureInfoWindow = null,
        ))
    }

    override fun onCreateLineClick() {
        acceptIntent(MapStore.Intent.Drawing.CreateLineClicked)
        startDrawing(MapScreenComponent.DrawingMode.LINE)
    }

    override fun onCreatePolygonClick() {
        acceptIntent(MapStore.Intent.Drawing.CreatePolygonClicked)
        startDrawing(MapScreenComponent.DrawingMode.POLYGON)
    }

    override fun onCreatePointLatitudeChange(value: String) {
        acceptIntent(MapStore.Intent.CreatePoint.LatitudeChanged(value))
        updateCreatePointDraft { copy(latitudeInput = value) }
    }

    override fun onCreatePointLongitudeChange(value: String) {
        acceptIntent(MapStore.Intent.CreatePoint.LongitudeChanged(value))
        updateCreatePointDraft { copy(longitudeInput = value) }
    }

    override fun onCreatePointTitleChange(value: String) {
        acceptIntent(MapStore.Intent.CreatePoint.TitleChanged(value))
        updateCreatePointDraft { copy(titleInput = value) }
    }

    override fun onCreatePointConfirm() {
        acceptIntent(MapStore.Intent.CreatePoint.Confirmed)
        val model = currentModel()
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

        setModel(model.copy(
            mapState = model.mapState.copy(
                points = model.mapState.points + point,
            ),
            isCreatePointSheetVisible = false,
            createPointDraft = null,
        ))
    }

    override fun onCreatePointSheetDismiss() {
        acceptIntent(MapStore.Intent.CreatePoint.SheetDismissed)
        setModel(currentModel().copy(
            isCreatePointSheetVisible = false,
            createPointDraft = null,
        ))
    }

    override fun onDrawingAddPositionClick() {
        acceptIntent(MapStore.Intent.Drawing.AddPositionClicked)
        val model = currentModel()
        val draft = model.shapeDrawingDraft ?: return
        val snapshot = model.lastCameraSnapshot ?: return
        setModel(model.copy(
            shapeDrawingDraft = shapeDrawingDraftUpdater.addVertex(draft, snapshot),
            selectedFeatureInfoWindow = null,
        ))
    }

    override fun onDrawingRemoveLastPositionClick() {
        acceptIntent(MapStore.Intent.Drawing.RemoveLastPositionClicked)
        val model = currentModel()
        val draft = model.shapeDrawingDraft ?: return
        setModel(model.copy(
            shapeDrawingDraft = shapeDrawingDraftUpdater.removeLastVertex(draft),
        ))
    }

    override fun onDrawingDetailsClick() {
        acceptIntent(MapStore.Intent.Drawing.DetailsClicked)
        val model = currentModel()
        val draft = model.shapeDrawingDraft ?: return
        if (!draft.canOpenDetails()) {
            return
        }
        setModel(model.copy(
            isCreateShapeSheetVisible = true,
        ))
    }

    override fun onDrawingDismiss() {
        acceptIntent(MapStore.Intent.Drawing.Dismissed)
        setModel(currentModel().copy(
            drawingMode = null,
            shapeDrawingDraft = null,
            isCreateShapeSheetVisible = false,
        ))
    }

    override fun onCreateShapeTitleChange(value: String) {
        acceptIntent(MapStore.Intent.Drawing.TitleChanged(value))
        val model = currentModel()
        val draft = model.shapeDrawingDraft ?: return
        setModel(model.copy(
            shapeDrawingDraft = draft.copy(titleInput = value),
        ))
    }

    override fun onCreateShapeConfirm() {
        acceptIntent(MapStore.Intent.Drawing.Confirmed)
        val model = currentModel()
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
                setModel(model.copy(
                    mapState = model.mapState.copy(lines = model.mapState.lines + line),
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                ))
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
                setModel(model.copy(
                    mapState = model.mapState.copy(polygons = model.mapState.polygons + polygon),
                    drawingMode = null,
                    shapeDrawingDraft = null,
                    isCreateShapeSheetVisible = false,
                ))
            }
        }
    }

    override fun onCreateShapeSheetDismiss() {
        acceptIntent(MapStore.Intent.Drawing.ShapeSheetDismissed)
        setModel(currentModel().copy(
            isCreateShapeSheetVisible = false,
        ))
    }

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) {
        acceptIntent(
            MapStore.Intent.FeatureSelection.FeatureClicked(
                featureKey = featureKey,
                featureType = featureType.toStoreFeatureType(),
                anchor = anchor.toStoreAnchor(),
            ),
        )
        val model = currentModel()
        val feature = featureSelectionResolver.resolve(model.mapState, featureKey, featureType) ?: return
        setModel(model.copy(
            isMapToolsMenuVisible = false,
            isCenterMarkerMenuVisible = false,
            selectedFeatureInfoWindow = featureInfoWindowStateMapper.map(feature, anchor),
        ))
    }

    override fun onFeatureInfoWindowDismiss() {
        acceptIntent(MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed)
        setModel(currentModel().copy(
            selectedFeatureInfoWindow = null,
        ))
    }

    private fun startDrawing(mode: MapScreenComponent.DrawingMode) {
        val model = currentModel()
        setModel(model.copy(
            isMapToolsMenuVisible = false,
            isCenterMarkerMenuVisible = false,
            isCreatePointSheetVisible = false,
            createPointDraft = null,
            drawingMode = mode,
            shapeDrawingDraft = MapScreenComponent.ShapeDrawingDraft(mode = mode),
            isCreateShapeSheetVisible = false,
            selectedFeatureInfoWindow = null,
        ))
    }

    private fun defaultModel(): MapScreenComponent.Model =
        MapScreenComponent.Model()

    private fun recalculateRulerState(model: MapScreenComponent.Model): MapScreenComponent.Model {
        if (!model.isRulerEnabled) {
            return clearRulerState(model)
        }

        val snapshot = model.lastCameraSnapshot
        val updatedModel = if (model.currentLocationMarker == null && snapshot != null) {
            model.copy(
                myLocationMode = MyLocationMode.MANUAL_PLACEHOLDER,
                currentLocationMarker = snapshot.toPlaceholderLocationMarker(),
            )
        } else {
            model
        }

        val marker = updatedModel.currentLocationMarker ?: return clearRulerState(updatedModel)
        val endSnapshot = updatedModel.lastCameraSnapshot ?: return clearRulerState(updatedModel)
        val measurement = rulerMeasurementCalculator.calculate(
            startLatitude = marker.latitude,
            startLongitude = marker.longitude,
            endLatitude = endSnapshot.latitude,
            endLongitude = endSnapshot.longitude,
        )
        return updatedModel.copy(
            rulerMeasurement = measurement,
            rulerInfoWindow = rulerInfoWindowStateFormatter.format(measurement),
        )
    }

    private fun clearRulerState(model: MapScreenComponent.Model): MapScreenComponent.Model =
        model.copy(
            rulerMeasurement = null,
            rulerInfoWindow = null,
        )

    private fun updateCreatePointDraft(
        transform: MapScreenComponent.CreatePointDraft.() -> MapScreenComponent.CreatePointDraft,
    ) {
        val model = currentModel()
        val draft = model.createPointDraft ?: return
        setModel(model.copy(
            createPointDraft = draft.transform(),
        ))
    }

    private fun currentModel(): MapScreenComponent.Model = model.value

    private fun acceptIntent(intent: MapStore.Intent) {
        mapStoreHolder.accept(intent)
    }

    private fun setModel(model: MapScreenComponent.Model) {
        mapStoreHolder.updateModel(model)
    }

    private fun MapCameraSnapshot.toCreatePointDraft(): MapScreenComponent.CreatePointDraft =
        MapScreenComponent.CreatePointDraft(
            latitudeInput = latitude.toString(),
            longitudeInput = longitude.toString(),
        )

    private fun MapCameraSnapshot.toPlaceholderLocationMarker(): MapLocationMarker =
        MapLocationMarker(
            latitude = latitude,
            longitude = longitude,
            isPlaceholder = true,
        )

    private fun MapScreenComponent.ShapeDrawingDraft.canOpenDetails(): Boolean =
        fixedVertices.size >= minimumVertexCount()

    private fun MapScreenComponent.ShapeDrawingDraft.minimumVertexCount(): Int =
        when (mode) {
            MapScreenComponent.DrawingMode.LINE -> 2
            MapScreenComponent.DrawingMode.POLYGON -> 3
        }

    private companion object {
        const val MAP_STORE_HOLDER_KEY = "DefaultMapScreenComponent.mapStoreHolder"
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
