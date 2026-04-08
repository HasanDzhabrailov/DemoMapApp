package ru.tech.demomapapp.feature.map.router

internal sealed interface MapRouterMessage {
    data class OverlayInteractionProcessed(val target: MapRouterStore.OverlayTarget) : MapRouterMessage
    data class FeatureInfoWindowUpdated(
        val infoWindow: ru.tech.demomapapp.feature.map.api.MapScreenComponent.FeatureInfoWindow?,
    ) : MapRouterMessage
    data class ViewportCommandUpdated(
        val source: MapRouterStore.ViewportCommandSource,
        val command: ru.tech.demomapapp.feature.map.api.MapViewportCommand?,
    ) : MapRouterMessage
    data object ViewportCommandConsumed : MapRouterMessage
    data class RulerEnabledUpdated(val enabled: Boolean) : MapRouterMessage
}
