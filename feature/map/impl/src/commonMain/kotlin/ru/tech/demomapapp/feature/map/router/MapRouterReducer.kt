package ru.tech.demomapapp.feature.map.router

import com.arkivanov.mvikotlin.core.store.Reducer

internal class MapRouterReducer : Reducer<MapRouterStore.State, MapRouterMessage> {

    override fun MapRouterStore.State.reduce(msg: MapRouterMessage): MapRouterStore.State = when (msg) {
        is MapRouterMessage.OverlayInteractionProcessed ->
            copy(
                selectedFeatureInfoWindow = if (msg.target.clearFeatureInfoWindow) {
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

        is MapRouterMessage.RulerEnabledUpdated -> copy(isRulerEnabled = msg.enabled)
    }
}
