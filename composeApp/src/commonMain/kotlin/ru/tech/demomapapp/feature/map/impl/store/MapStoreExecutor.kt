package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.impl.store.handler.CreatePointHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.DrawingHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.FeatureClickHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.LocationHandler

internal class MapStoreExecutor(
    private val createPointHandler: CreatePointHandler,
    private val drawingHandler: DrawingHandler,
    private val locationHandler: LocationHandler,
    private val featureClickHandler: FeatureClickHandler,
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
            is MapStore.Intent.CenterMarker.MenuDismissed -> callbacks.onMessage(
                MapStoreMessage.CenterMarkerMenuDismissed,
            )
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
            is MapStore.Intent.CreatePoint.SheetDismissed -> callbacks.onMessage(
                MapStoreMessage.CreatePointSheetDismissed,
            )
            is MapStore.Intent.CreatePoint.Confirmed -> handleCreatePointConfirm()
            is MapStore.Intent.Drawing.CreateLineClicked -> callbacks.onMessage(
                MapStoreMessage.DrawingStarted(MapStore.DrawingMode.LINE),
            )
            is MapStore.Intent.Drawing.CreatePolygonClicked -> callbacks.onMessage(
                MapStoreMessage.DrawingStarted(MapStore.DrawingMode.POLYGON),
            )
            is MapStore.Intent.Drawing.AddPositionClicked -> handleDrawingAddPosition()
            is MapStore.Intent.Drawing.RemoveLastPositionClicked -> callbacks.onMessage(
                MapStoreMessage.DrawingLastPositionRemoved,
            )
            is MapStore.Intent.Drawing.DetailsClicked -> callbacks.onMessage(MapStoreMessage.ShapeSheetOpened)
            is MapStore.Intent.Drawing.Dismissed -> callbacks.onMessage(MapStoreMessage.DrawingDismissed)
            is MapStore.Intent.Drawing.ShapeSheetDismissed -> callbacks.onMessage(MapStoreMessage.ShapeSheetDismissed)
            is MapStore.Intent.Drawing.TitleChanged -> callbacks.onMessage(
                MapStoreMessage.ShapeTitleChanged(intent.value),
            )
            is MapStore.Intent.Drawing.Confirmed -> handleDrawingConfirm()
            is MapStore.Intent.FeatureSelection.FeatureClicked -> handleFeatureClick(intent)
            is MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed -> callbacks.onMessage(
                MapStoreMessage.FeatureInfoWindowDismissed,
            )
            is MapStore.Intent.Location.GpsToggled -> handleGpsToggle()
            is MapStore.Intent.Location.MyLocationClicked -> handleMyLocationClick()
            is MapStore.Intent.Location.CurrentLocationFocusClicked -> handleCurrentLocationFocusClick()
            is MapStore.Intent.Location.LocationRequestConsumed -> Unit
            is MapStore.Intent.Location.LocationResultReceived -> handleLocationResult(intent.result)
            is MapStore.Intent.Viewport.CameraIdle -> handleCameraIdle(intent.snapshot)
            is MapStore.Intent.Viewport.ViewportCommandConsumed -> Unit
            is MapStore.Intent.Viewport.ZoomInClicked -> emitViewportCommand(MapViewportCommand.ZoomIn)
            is MapStore.Intent.Viewport.ZoomOutClicked -> emitViewportCommand(MapViewportCommand.ZoomOut)
            is MapStore.Intent.Tools.AvailableMapsClicked -> callbacks.onMessage(MapStoreMessage.AvailableMapsOpened)
            is MapStore.Intent.Tools.AvailableMapsDismissed -> callbacks.onMessage(
                MapStoreMessage.AvailableMapsDismissed,
            )
            is MapStore.Intent.Tools.AvailableMapSelected -> callbacks.onMessage(
                MapStoreMessage.AvailableMapSelected(intent.mapId),
            )
            is MapStore.Intent.Tools.AvailableMapConfirmed -> callbacks.onMessage(MapStoreMessage.AvailableMapConfirmed)
            is MapStore.Intent.Tools.AvailableMapSelectionDismissed -> callbacks.onMessage(
                MapStoreMessage.AvailableMapSelectionDismissed,
            )
            is MapStore.Intent.Tools.MapsOnScreenClicked -> callbacks.onMessage(MapStoreMessage.MapsOnScreenOpened)
            is MapStore.Intent.Tools.MapsOnScreenDismissed -> callbacks.onMessage(MapStoreMessage.MapsOnScreenDismissed)
            is MapStore.Intent.Tools.OverlayLayerActionsClicked -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerActionsOpened(intent.layerId),
            )
            is MapStore.Intent.Tools.OverlayLayerActionsDismissed -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerActionsDismissed,
            )
            is MapStore.Intent.Tools.OverlayLayerMoveUpClicked -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerMovedUp,
            )
            is MapStore.Intent.Tools.OverlayLayerMoveDownClicked -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerMovedDown,
            )
            is MapStore.Intent.Tools.OverlayLayerRemoveClicked -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerRemoved,
            )
            is MapStore.Intent.Tools.OverlayLayerOpacityClicked -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerOpacityEditorOpened,
            )
            is MapStore.Intent.Tools.OverlayLayerOpacityChanged -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerOpacityChanged(intent.value),
            )
            is MapStore.Intent.Tools.OverlayLayerOpacityDismissed -> callbacks.onMessage(
                MapStoreMessage.OverlayLayerOpacityEditorDismissed,
            )
            is MapStore.Intent.Tools.MapToolsDismissed -> callbacks.onMessage(MapStoreMessage.MapToolsMenuDismissed)
            is MapStore.Intent.Tools.MapToolsClicked -> callbacks.onMessage(MapStoreMessage.MapToolsMenuToggled)
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun handleCreatePointConfirm() {
        createPointHandler.handleConfirm(
            state = currentState(),
            onCreated = { message -> callbacks.onMessage(message) },
        )
    }

    private fun handleDrawingAddPosition() {
        drawingHandler.handleAddPosition(
            snapshot = currentState().lastCameraSnapshot,
            onPositionAdded = { message -> callbacks.onMessage(message) },
        )
    }

    private fun handleDrawingConfirm() {
        drawingHandler.handleConfirm(
            state = currentState(),
            onLineCreated = { message -> callbacks.onMessage(message) },
            onPolygonCreated = { message -> callbacks.onMessage(message) },
        )
    }

    private fun handleFeatureClick(intent: MapStore.Intent.FeatureSelection.FeatureClicked) {
        featureClickHandler.handleFeatureClick(
            state = currentState(),
            featureKey = intent.featureKey,
            featureType = intent.featureType,
            anchor = intent.anchor,
            onInfoWindowOpened = { message -> callbacks.onMessage(message) },
        )
    }

    private fun handleGpsToggle() {
        val result = locationHandler.handleGpsToggle(currentState())
        syncState(result.state)
        result.locationRequest?.let { request ->
            callbacks.onLabel(MapStore.Label.Location.RequestIssued(request))
        }
    }

    private fun handleMyLocationClick() {
        val result = locationHandler.handleMyLocationClick(currentState()) ?: return
        syncState(result.state)
    }

    private fun handleCurrentLocationFocusClick() {
        val result = locationHandler.handleCurrentLocationFocusClick(currentState()) ?: return
        result.viewportCommand?.let { command ->
            emitViewportCommand(command)
        }
        if (result.state != currentState()) {
            syncState(result.state)
        }
        result.locationRequest?.let { request ->
            callbacks.onLabel(MapStore.Label.Location.RequestIssued(request))
        }
    }

    private fun handleLocationResult(result: LocationRequestResult) {
        val locationResult = locationHandler.handleLocationResult(
            state = currentState(),
            result = result,
        )
        syncState(locationResult.state)
        locationResult.viewportCommand?.let { command ->
            callbacks.onLabel(MapStore.Label.Viewport.CommandRequested(command))
        }
    }

    private fun handleCameraIdle(snapshot: MapCameraSnapshot) {
        callbacks.onMessage(MapStoreMessage.CameraIdleReceived(snapshot))
    }

    private fun emitViewportCommand(command: MapViewportCommand) {
        callbacks.onLabel(MapStore.Label.Viewport.CommandRequested(command))
    }

    private fun currentState() = callbacks.state

    private fun syncState(state: MapStore.State) {
        callbacks.onMessage(MapStoreMessage.StateSynced(state))
    }
}
