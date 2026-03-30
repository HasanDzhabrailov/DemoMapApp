package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.recalculateRulerState

internal class MapStoreExecutor(
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
            is MapStore.Intent.Drawing.DetailsClicked -> callbacks.onMessage(MapStoreMessage.ShapeSheetOpened)
            is MapStore.Intent.Drawing.Dismissed -> callbacks.onMessage(MapStoreMessage.DrawingDismissed)
            is MapStore.Intent.Drawing.ShapeSheetDismissed -> callbacks.onMessage(MapStoreMessage.ShapeSheetDismissed)
            is MapStore.Intent.Drawing.TitleChanged -> callbacks.onMessage(MapStoreMessage.ShapeTitleChanged(intent.value))
            is MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed -> {
                callbacks.onMessage(MapStoreMessage.FeatureInfoWindowDismissed)
            }
            is MapStore.Intent.Location.CurrentLocationFocusClicked -> handleCurrentLocationFocusClick()
            is MapStore.Intent.Location.GpsToggled -> handleGpsToggle()
            is MapStore.Intent.Location.LocationRequestConsumed -> Unit
            is MapStore.Intent.Location.LocationResultReceived -> handleLocationResult(intent.result)
            is MapStore.Intent.Viewport.ViewportCommandConsumed -> Unit
            is MapStore.Intent.Viewport.ZoomInClicked -> emitViewportCommand(MapViewportCommand.ZoomIn)
            is MapStore.Intent.Viewport.ZoomOutClicked -> emitViewportCommand(MapViewportCommand.ZoomOut)
            is MapStore.Intent.Location,
            is MapStore.Intent.Ruler,
            is MapStore.Intent.CreatePoint.Confirmed,
            is MapStore.Intent.Drawing.AddPositionClicked,
            is MapStore.Intent.Drawing.Confirmed,
            is MapStore.Intent.Drawing.RemoveLastPositionClicked,
            is MapStore.Intent.FeatureSelection.FeatureClicked,
            is MapStore.Intent.Viewport.CameraIdle,
            -> Unit
            is MapStore.Intent.SyncState -> callbacks.onMessage(MapStoreMessage.StateSynced(intent.state))
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
        val model = currentModel()
        if (currentState().activeLocationRequest == MapLocationRequest.EnableGpsLocationRequest) {
            syncModel(
                model = recalculateRulerState(
                    model.copy(
                        myLocationMode = MyLocationMode.OFF,
                        currentLocationMarker = null,
                        pendingLocationRequest = null,
                    ),
                    rulerMeasurementCalculator = rulerMeasurementCalculator,
                    rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
                ),
                activeLocationRequest = null,
            )
            return
        }

        val updatedModel = recalculateRulerState(
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
            rulerMeasurementCalculator = rulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
        )
        val nextActiveLocationRequest = if (updatedModel.pendingLocationRequest != null) {
            updatedModel.pendingLocationRequest
        } else {
            null
        }
        syncModel(updatedModel, activeLocationRequest = nextActiveLocationRequest)
        if (updatedModel.pendingLocationRequest != null) {
            callbacks.onLabel(MapStore.Label.Location.RequestIssued(updatedModel.pendingLocationRequest))
        }
    }

    private fun handleCurrentLocationFocusClick() {
        val model = currentModel()
        when {
            model.currentLocationMarker != null -> emitViewportCommand(
                MapViewportCommand.MoveTo(
                    latitude = model.currentLocationMarker.latitude,
                    longitude = model.currentLocationMarker.longitude,
                ),
            )

            model.myLocationMode == MyLocationMode.GPS -> {
                val updatedModel = model.copy(
                    pendingLocationRequest = MapLocationRequest.RecenterToGpsLocationRequest,
                )
                syncModel(
                    updatedModel,
                    activeLocationRequest = MapLocationRequest.RecenterToGpsLocationRequest,
                )
                callbacks.onLabel(MapStore.Label.Location.RequestIssued(MapLocationRequest.RecenterToGpsLocationRequest))
            }
        }
    }

    private fun handleLocationResult(result: LocationRequestResult) {
        val model = currentModel()
        val request = currentState().activeLocationRequest
        val updatedModel = recalculateRulerState(
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
            rulerMeasurementCalculator = rulerMeasurementCalculator,
            rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
        )
        syncModel(updatedModel, activeLocationRequest = null)
        updatedModel.pendingViewportCommand?.let { command ->
            callbacks.onLabel(MapStore.Label.Viewport.CommandRequested(command))
        }
    }

    private fun emitViewportCommand(command: MapViewportCommand) {
        callbacks.onLabel(MapStore.Label.Viewport.CommandRequested(command))
    }

    private fun currentState() = callbacks.state

    private fun currentModel() = callbacks.state.toModel()

    private fun syncModel(
        model: ru.tech.demomapapp.feature.map.api.MapScreenComponent.Model,
        activeLocationRequest: MapLocationRequest? = currentState().activeLocationRequest,
    ) {
        callbacks.onMessage(
            MapStoreMessage.StateSynced(
                MapStore.State.fromModel(
                    model = model,
                    activeLocationRequest = activeLocationRequest,
                ),
            ),
        )
    }
}
