package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Reducer

internal class MapRouterReducer : Reducer<MapRouterStore.State, MapRouterMessage> {

    override fun MapRouterStore.State.reduce(msg: MapRouterMessage): MapRouterStore.State = when (msg) {
        is MapRouterMessage.ViewportStateUpdated ->
            copy(viewportState = msg.state)

        is MapRouterMessage.ToolsStateUpdated ->
            copy(toolsState = msg.state)

        is MapRouterMessage.LocationStateUpdated ->
            copy(locationState = msg.state)

        is MapRouterMessage.RulerStateUpdated ->
            copy(rulerState = msg.state)

        is MapRouterMessage.CenterMarkerStateUpdated ->
            copy(centerMarkerState = msg.state)

        is MapRouterMessage.CreatePointStateUpdated ->
            copy(createPointState = msg.state)

        is MapRouterMessage.DrawingStateUpdated ->
            copy(drawingState = msg.state)

        is MapRouterMessage.FeatureSelectionStateUpdated ->
            copy(featureSelectionState = msg.state)
    }
}
