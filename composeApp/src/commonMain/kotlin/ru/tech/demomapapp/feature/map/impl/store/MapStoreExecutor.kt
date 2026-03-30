package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.impl.CreateMapLineInput
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.MapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.MapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.TimeProvider

internal class MapStoreExecutor(
    private val createMapPointUseCase: CreateMapPointUseCase,
    private val createMapLineUseCase: CreateMapLineUseCase,
    private val createMapPolygonUseCase: CreateMapPolygonUseCase,
    private val timeProvider: TimeProvider,
    private val featureIdProvider: FeatureIdProvider,
    private val featureSelectionResolver: MapFeatureSelectionResolver,
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper,
    private val rulerMeasurementCalculator: RulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter,
) : com.arkivanov.mvikotlin.core.store.Executor<
    MapStore.Intent,
    Nothing,
    MapStore.State,
    MapStoreMessage,
    MapStore.Label,
    > {

    private lateinit var callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
        MapStore.State,
        MapStoreMessage,
        Nothing,
        MapStore.Label,
    >

    override fun init(
        callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
            MapStore.State,
            MapStoreMessage,
            Nothing,
            MapStore.Label,
        >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: MapStore.Intent) {
        when (intent) {
            is MapStore.Intent.CenterMarker.Clicked -> callbacks.onMessage(MapStoreMessage.CenterMarkerMenuOpened)
            is MapStore.Intent.CenterMarker.MenuDismissed -> callbacks.onMessage(MapStoreMessage.CenterMarkerMenuDismissed)
            is MapStore.Intent.CreatePoint.Clicked -> callbacks.onMessage(MapStoreMessage.CreatePointSheetOpened)
            is MapStore.Intent.CreatePoint.LatitudeChanged -> callbacks.onMessage(
                MapStoreMessage.CreatePointLatitudeChanged(intent.value),
            )
            is MapStore.Intent.CreatePoint.LongitudeChanged -> callbacks.onMessage(
                MapStoreMessage.CreatePointLongitudeChanged(intent.value),
            )
            is MapStore.Intent.CreatePoint.TitleChanged -> callbacks.onMessage(
                MapStoreMessage.CreatePointTitleChanged(intent.value),
            )
            is MapStore.Intent.CreatePoint.SheetDismissed -> callbacks.onMessage(MapStoreMessage.CreatePointSheetDismissed)
            is MapStore.Intent.Drawing.CreateLineClicked -> callbacks.onMessage(
                MapStoreMessage.DrawingStarted(MapStore.DrawingMode.LINE),
            )
            is MapStore.Intent.Drawing.CreatePolygonClicked -> callbacks.onMessage(
                MapStoreMessage.DrawingStarted(MapStore.DrawingMode.POLYGON),
            )
            is MapStore.Intent.Drawing.AddPositionClicked -> handleDrawingAddPositionClick()
            is MapStore.Intent.Drawing.RemoveLastPositionClicked -> {
                callbacks.onMessage(MapStoreMessage.DrawingLastPositionRemoved)
            }
            is MapStore.Intent.Drawing.DetailsClicked -> callbacks.onMessage(MapStoreMessage.ShapeSheetOpened)
            is MapStore.Intent.Drawing.Dismissed -> callbacks.onMessage(MapStoreMessage.DrawingDismissed)
            is MapStore.Intent.Drawing.ShapeSheetDismissed -> callbacks.onMessage(MapStoreMessage.ShapeSheetDismissed)
            is MapStore.Intent.Drawing.TitleChanged -> callbacks.onMessage(MapStoreMessage.ShapeTitleChanged(intent.value))
            is MapStore.Intent.Drawing.Confirmed -> handleCreateShapeConfirm()
            is MapStore.Intent.FeatureSelection.FeatureClicked -> handleFeatureClick(intent)
            is MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed -> {
                callbacks.onMessage(MapStoreMessage.FeatureInfoWindowDismissed)
            }
            is MapStore.Intent.Location.MyLocationClicked -> handleMyLocationClick()
            is MapStore.Intent.Location.CurrentLocationFocusClicked -> handleCurrentLocationFocusClick()
            is MapStore.Intent.Location.GpsToggled -> handleGpsToggle()
            is MapStore.Intent.Location.LocationRequestConsumed -> Unit
            is MapStore.Intent.Location.LocationResultReceived -> handleLocationResult(intent.result)
            is MapStore.Intent.CreatePoint.Confirmed -> handleCreatePointConfirm()
            is MapStore.Intent.Ruler.Toggled -> handleRulerToggle()
            is MapStore.Intent.Viewport.CameraIdle -> handleCameraIdle(intent.snapshot)
            is MapStore.Intent.Viewport.ViewportCommandConsumed -> Unit
            is MapStore.Intent.Viewport.ZoomInClicked -> emitViewportCommand(MapViewportCommand.ZoomIn)
            is MapStore.Intent.Viewport.ZoomOutClicked -> emitViewportCommand(MapViewportCommand.ZoomOut)
            is MapStore.Intent.Location,
            -> Unit
            is MapStore.Intent.Tools.AvailableMapsClicked,
            is MapStore.Intent.Tools.MapToolsDismissed,
            is MapStore.Intent.Tools.MapsOnScreenClicked,
            -> callbacks.onMessage(MapStoreMessage.MapToolsMenuDismissed)
            is MapStore.Intent.Tools.MapToolsClicked -> callbacks.onMessage(MapStoreMessage.MapToolsMenuToggled)
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun handleGpsToggle() {
        if (currentState().activeLocationRequest == MapLocationRequest.EnableGpsLocationRequest) {
            val updatedState = currentState().copy(
                myLocationMode = MyLocationMode.OFF,
                currentLocationMarker = null,
                activeLocationRequest = null,
            )
            syncState(updatedState)
            publishRulerState(updatedState)
            return
        }

        val updatedState =
            when (currentState().myLocationMode) {
                MyLocationMode.GPS -> currentState().copy(
                    myLocationMode = MyLocationMode.OFF,
                    currentLocationMarker = null,
                    activeLocationRequest = null,
                )

                MyLocationMode.OFF,
                MyLocationMode.MANUAL_PLACEHOLDER,
                -> currentState().copy(
                    myLocationMode = MyLocationMode.OFF,
                    currentLocationMarker = null,
                    activeLocationRequest = MapLocationRequest.EnableGpsLocationRequest,
                )
            }
        syncState(updatedState)
        publishRulerState(updatedState)
        updatedState.activeLocationRequest?.let { request ->
            callbacks.onLabel(MapStore.Label.Location.RequestIssued(request))
        }
    }

    private fun handleMyLocationClick() {
        val state = currentState()
        if (state.myLocationMode == MyLocationMode.GPS) {
            return
        }

        val snapshot = state.lastCameraSnapshot ?: return
        val updatedState = state.copy(
            myLocationMode = MyLocationMode.MANUAL_PLACEHOLDER,
            currentLocationMarker = snapshot.toPlaceholderLocationMarker(),
            activeLocationRequest = null,
        )
        syncState(updatedState)
        publishRulerState(updatedState)
    }

    private fun handleCurrentLocationFocusClick() {
        val state = currentState()
        when {
            state.currentLocationMarker != null -> emitViewportCommand(
                MapViewportCommand.MoveTo(
                    latitude = state.currentLocationMarker.latitude,
                    longitude = state.currentLocationMarker.longitude,
                ),
            )

            state.myLocationMode == MyLocationMode.GPS -> {
                val updatedState = state.copy(
                    activeLocationRequest = MapLocationRequest.RecenterToGpsLocationRequest,
                )
                syncState(updatedState)
                callbacks.onLabel(MapStore.Label.Location.RequestIssued(MapLocationRequest.RecenterToGpsLocationRequest))
            }
        }
    }

    private fun handleLocationResult(result: LocationRequestResult) {
        val state = currentState()
        val request = currentState().activeLocationRequest
        val updatedState = when (result) {
                LocationRequestResult.PermissionDenied -> {
                    state.copy(
                        myLocationMode = MyLocationMode.OFF,
                        currentLocationMarker = null,
                        activeLocationRequest = null,
                    )
                }

                LocationRequestResult.LocationUnavailable -> {
                    if (state.myLocationMode == MyLocationMode.GPS && request != MapLocationRequest.EnableGpsLocationRequest) {
                        state.copy(
                            activeLocationRequest = null,
                        )
                    } else {
                        state.copy(
                            myLocationMode = MyLocationMode.OFF,
                            currentLocationMarker = null,
                            activeLocationRequest = null,
                        )
                    }
                }

                is LocationRequestResult.LocationResolved -> state.copy(
                    myLocationMode = MyLocationMode.GPS,
                    currentLocationMarker = MapLocationMarker(
                        latitude = result.latitude,
                        longitude = result.longitude,
                        isPlaceholder = false,
                    ),
                    activeLocationRequest = null,
                )
            }
        syncState(updatedState)
        publishRulerState(updatedState)
        if (result is LocationRequestResult.LocationResolved) {
            callbacks.onLabel(
                MapStore.Label.Viewport.CommandRequested(
                    MapViewportCommand.MoveTo(
                        latitude = result.latitude,
                        longitude = result.longitude,
                    ),
                ),
            )
        }
    }

    private fun handleRulerToggle() {
        val state = currentState()
        if (state.isRulerEnabled) {
            callbacks.onMessage(MapStoreMessage.RulerDisabled)
            return
        }

        val updatedState = state.copy(isRulerEnabled = true)
        callbacks.onMessage(MapStoreMessage.RulerEnabled)
        publishRulerState(updatedState)
    }

    private fun handleCreatePointConfirm() {
        val draft = currentState().createPointDraft ?: return
        val point = createMapPointUseCase.create(
            CreateMapPointInput(
                id = featureIdProvider.nextId(),
                latitudeInput = draft.latitudeInput,
                longitudeInput = draft.longitudeInput,
                titleInput = draft.titleInput,
                createdAtEpochMillis = timeProvider.currentTimeMillis(),
            ),
        ) ?: return

        callbacks.onMessage(MapStoreMessage.CreatePointCreated(point))
    }

    private fun handleDrawingAddPositionClick() {
        val snapshot = currentState().lastCameraSnapshot ?: return
        callbacks.onMessage(MapStoreMessage.DrawingPositionAdded(snapshot))
    }

    private fun handleCreateShapeConfirm() {
        val draft = currentState().shapeDrawingDraft ?: return
        val createdAt = timeProvider.currentTimeMillis()
        val id = featureIdProvider.nextId()

        when (draft.mode) {
            MapStore.DrawingMode.LINE -> {
                val line = createMapLineUseCase.create(
                    CreateMapLineInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                ) ?: return
                callbacks.onMessage(MapStoreMessage.LineCreated(line))
            }

            MapStore.DrawingMode.POLYGON -> {
                val polygon = createMapPolygonUseCase.create(
                    CreateMapPolygonInput(
                        id = id,
                        vertices = draft.fixedVertices,
                        titleInput = draft.titleInput,
                        createdAtEpochMillis = createdAt,
                    ),
                ) ?: return
                callbacks.onMessage(MapStoreMessage.PolygonCreated(polygon))
            }
        }
    }

    private fun handleFeatureClick(intent: MapStore.Intent.FeatureSelection.FeatureClicked) {
        val feature = featureSelectionResolver.resolve(
            mapState = currentState().mapState,
            featureKey = intent.featureKey,
            featureType = intent.featureType,
        ) ?: return

        callbacks.onMessage(
            MapStoreMessage.FeatureInfoWindowOpened(
                infoWindow = featureInfoWindowStateMapper.map(
                    feature = feature,
                    anchor = intent.anchor,
                ),
            ),
        )
    }

    private fun handleCameraIdle(snapshot: MapCameraSnapshot) {
        callbacks.onMessage(MapStoreMessage.CameraIdleReceived(snapshot))
        publishRulerState(
            currentState().copy(
                lastCameraSnapshot = snapshot,
                selectedFeatureInfoWindow = null,
            ),
        )
    }

    private fun emitViewportCommand(command: MapViewportCommand) {
        callbacks.onLabel(MapStore.Label.Viewport.CommandRequested(command))
    }

    private fun currentState() = callbacks.state

    private fun syncState(state: MapStore.State) {
        callbacks.onMessage(MapStoreMessage.StateSynced(state))
    }

    private fun publishRulerState(state: MapStore.State) {
        val rulerResolution = state.resolveRulerState(
            rulerMeasurementCalculator = rulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
        )

        rulerResolution.fallbackMarker?.let { marker ->
            callbacks.onMessage(
                MapStoreMessage.CurrentLocationMarkerUpdated(
                    mode = MyLocationMode.MANUAL_PLACEHOLDER,
                    marker = marker,
                ),
            )
        }

        val measurement = rulerResolution.measurement
        val infoWindow = rulerResolution.infoWindow
        if (measurement != null && infoWindow != null) {
            callbacks.onMessage(MapStoreMessage.RulerMeasurementUpdated(measurement, infoWindow))
        } else {
            callbacks.onMessage(MapStoreMessage.RulerCleared)
        }
    }

    private fun MapCameraSnapshot.toPlaceholderLocationMarker(): MapLocationMarker =
        MapLocationMarker(
            latitude = latitude,
            longitude = longitude,
            isPlaceholder = true,
        )
}
