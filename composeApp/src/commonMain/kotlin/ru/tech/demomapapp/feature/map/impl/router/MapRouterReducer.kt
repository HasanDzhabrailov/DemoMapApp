package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Reducer

internal class MapRouterReducer : Reducer<MapRouterStore.State, MapRouterMessage> {

    override fun MapRouterStore.State.reduce(msg: MapRouterMessage): MapRouterStore.State = when (msg) {
        is MapRouterMessage.ViewportStateUpdated ->
            copy(
                viewportState = msg.state,
                selectedFeatureInfoWindow = if (msg.state.lastCameraSnapshot != viewportState?.lastCameraSnapshot) {
                    null
                } else {
                    selectedFeatureInfoWindow
                },
            )

        is MapRouterMessage.ToolsStateUpdated ->
            copy(
                toolsState = msg.state,
                selectedFeatureInfoWindow = if (
                    msg.state.isMapToolsMenuVisible ||
                    msg.state.isAvailableMapsSheetVisible ||
                    msg.state.isMapsOnScreenSheetVisible
                ) {
                    null
                } else {
                    selectedFeatureInfoWindow
                },
            )

        is MapRouterMessage.LocationStateUpdated ->
            copy(locationState = msg.state)

        is MapRouterMessage.RulerStateUpdated ->
            copy(rulerState = msg.state)

        is MapRouterMessage.CenterMarkerStateUpdated ->
            copy(centerMarkerState = msg.state)

        is MapRouterMessage.DrawingStateUpdated ->
            copy(
                drawingState = msg.state,
                selectedFeatureInfoWindow = if (
                    msg.state.isCreatePointSheetVisible ||
                    msg.state.mode != null ||
                    msg.state.shapeDraft != drawingState?.shapeDraft
                ) {
                    null
                } else {
                    selectedFeatureInfoWindow
                },
            )

        is MapRouterMessage.FeatureInfoWindowUpdated ->
            copy(selectedFeatureInfoWindow = msg.infoWindow)

        is MapRouterMessage.ViewportCommandUpdated -> when (msg.source) {
            MapRouterStore.ViewportCommandSource.VIEWPORT -> copy(
                viewportPendingCommand = msg.command,
                locationPendingViewportCommand = null,
                rulerPendingViewportCommand = null,
            )

            MapRouterStore.ViewportCommandSource.LOCATION -> copy(
                locationPendingViewportCommand = msg.command,
                rulerPendingViewportCommand = if (viewportPendingCommand == null) null else rulerPendingViewportCommand,
            )

            MapRouterStore.ViewportCommandSource.RULER -> copy(
                rulerPendingViewportCommand = msg.command,
            )
        }

        MapRouterMessage.ViewportCommandConsumed -> when (currentViewportCommandSource) {
            MapRouterStore.ViewportCommandSource.VIEWPORT -> copy(viewportPendingCommand = null)
            MapRouterStore.ViewportCommandSource.LOCATION -> copy(locationPendingViewportCommand = null)
            MapRouterStore.ViewportCommandSource.RULER -> copy(rulerPendingViewportCommand = null)
            null -> this
        }
    }
}
