package ru.tech.demomapapp.feature.map.impl.store

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.impl.store.handler.CreatePointHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.DrawingHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.FeatureClickHandler

internal class MapStoreExecutor(
    private val createPointHandler: CreatePointHandler,
    private val drawingHandler: DrawingHandler,
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
            is MapStore.Intent.CameraIdle -> handleCameraIdle(intent.snapshot)
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

    private fun handleCameraIdle(snapshot: MapCameraSnapshot) {
        callbacks.onMessage(MapStoreMessage.CameraIdleReceived(snapshot))
    }

    private fun currentState() = callbacks.state
}
