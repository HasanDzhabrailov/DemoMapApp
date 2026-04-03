package ru.tech.demomapapp.feature.map.impl.router

internal sealed interface MapRouterMessage {
    data class ViewportStateUpdated(val state: MapRouterStore.ChildState.Viewport) : MapRouterMessage
    data class ToolsStateUpdated(val state: MapRouterStore.ChildState.Tools) : MapRouterMessage
    data class LocationStateUpdated(val state: MapRouterStore.ChildState.Location) : MapRouterMessage
    data class RulerStateUpdated(val state: MapRouterStore.ChildState.Ruler) : MapRouterMessage
    data class CenterMarkerStateUpdated(val state: MapRouterStore.ChildState.CenterMarker) : MapRouterMessage
    data class DrawingStateUpdated(val state: MapRouterStore.ChildState.Drawing) : MapRouterMessage
    data class FeatureInfoWindowUpdated(
        val infoWindow: ru.tech.demomapapp.feature.map.api.MapScreenComponent.FeatureInfoWindow?,
    ) : MapRouterMessage
    data class ViewportCommandUpdated(
        val source: MapRouterStore.ViewportCommandSource,
        val command: ru.tech.demomapapp.feature.map.api.MapViewportCommand?,
    ) : MapRouterMessage
}
