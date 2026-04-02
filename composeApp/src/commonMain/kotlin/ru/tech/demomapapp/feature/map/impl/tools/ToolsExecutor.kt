package ru.tech.demomapapp.feature.map.impl.tools

import com.arkivanov.mvikotlin.core.store.Executor

internal class ToolsExecutor : Executor<
    ToolsStore.Intent,
    Nothing,
    ToolsStore.State,
    ToolsStore.Message,
    ToolsStore.Label,
    > {

    private lateinit var callbacks: Executor.Callbacks<
        ToolsStore.State,
        ToolsStore.Message,
        Nothing,
        ToolsStore.Label,
        >

    override fun init(
        callbacks: Executor.Callbacks<
            ToolsStore.State,
            ToolsStore.Message,
            Nothing,
            ToolsStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: ToolsStore.Intent) {
        when (intent) {
            is ToolsStore.Intent.MapToolsClicked -> dispatch(ToolsStore.Message.MapToolsMenuToggled)
            is ToolsStore.Intent.MapToolsDismissed -> dispatch(ToolsStore.Message.MapToolsMenuDismissed)
            is ToolsStore.Intent.AvailableMapsClicked -> dispatch(ToolsStore.Message.AvailableMapsOpened)
            is ToolsStore.Intent.AvailableMapsDismissed -> dispatch(ToolsStore.Message.AvailableMapsDismissed)
            is ToolsStore.Intent.AvailableMapSelected -> dispatch(
                ToolsStore.Message.AvailableMapSelected(intent.mapId),
            )
            is ToolsStore.Intent.AvailableMapConfirmed -> dispatch(
                ToolsStore.Message.AvailableMapConfirmed,
                emitLayersChanged = true,
            )
            is ToolsStore.Intent.AvailableMapSelectionDismissed -> {
                dispatch(ToolsStore.Message.AvailableMapSelectionDismissed)
            }
            is ToolsStore.Intent.MapsOnScreenClicked -> dispatch(ToolsStore.Message.MapsOnScreenOpened)
            is ToolsStore.Intent.MapsOnScreenDismissed -> dispatch(ToolsStore.Message.MapsOnScreenDismissed)
            is ToolsStore.Intent.LayerActionsClicked -> dispatch(
                ToolsStore.Message.LayerActionsOpened(intent.layerId),
            )
            is ToolsStore.Intent.LayerActionsDismissed -> dispatch(ToolsStore.Message.LayerActionsDismissed)
            is ToolsStore.Intent.MoveLayerUpClicked -> dispatch(
                ToolsStore.Message.LayerMovedUp,
                emitLayersChanged = true,
            )
            is ToolsStore.Intent.MoveLayerDownClicked -> dispatch(
                ToolsStore.Message.LayerMovedDown,
                emitLayersChanged = true,
            )
            is ToolsStore.Intent.RemoveLayerClicked -> dispatch(
                ToolsStore.Message.LayerRemoved,
                emitLayersChanged = true,
            )
            is ToolsStore.Intent.LayerOpacityClicked -> dispatch(ToolsStore.Message.LayerOpacityEditorOpened)
            is ToolsStore.Intent.LayerOpacityChanged -> dispatch(
                ToolsStore.Message.LayerOpacityChanged(intent.value),
                emitLayersChanged = true,
            )
            is ToolsStore.Intent.LayerOpacityDismissed -> dispatch(ToolsStore.Message.LayerOpacityEditorDismissed)
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun dispatch(message: ToolsStore.Message, emitLayersChanged: Boolean = false) {
        val currentState = callbacks.state
        callbacks.onMessage(message)
        if (!emitLayersChanged) {
            return
        }
        val updatedState = ToolsReducer.reduce(currentState, message)
        if (updatedState.layers != currentState.layers) {
            callbacks.onLabel(ToolsStore.Label.LayersChanged(updatedState.layers))
        }
    }
}
