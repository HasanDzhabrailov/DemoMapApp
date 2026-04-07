package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal interface MapRouterStore : Store<MapRouterStore.Intent, MapRouterStore.State, MapRouterStore.Label> {

    sealed interface Action

    sealed interface Intent {
        // Cross-feature coordination only - child-private state is managed by child components

        // Overlay management
        data class OverlayInteractionRequested(val target: OverlayTarget) : Intent
        data object ToolsMenuDismissRequested : Intent
        data object ViewportMenuDismissRequested : Intent

        // Feature selection - render data passed as parameters since it's owned by child components
        data class FeatureClicked(
            val points: List<ru.tech.demomapapp.feature.map.api.MapPoint>,
            val lines: List<ru.tech.demomapapp.feature.map.api.MapLine>,
            val polygons: List<ru.tech.demomapapp.feature.map.api.MapPolygon>,
            val featureKey: String,
            val featureType: MapScreenComponent.FeatureType,
            val anchor: MapScreenComponent.FeatureInfoWindowAnchor,
        ) : Intent
        data object FeatureInfoWindowDismissed : Intent

        // Center marker actions
        data object CenterMarkerClicked : Intent

        // Viewport commands (cross-feature coordination)
        data class ViewportCommandUpdated(
            val source: ViewportCommandSource,
            val command: MapViewportCommand?,
        ) : Intent
        data object ViewportCommandConsumed : Intent
        
        // Ruler toggle (cross-feature - affects viewport and drawing)
        data class RulerEnabledUpdated(val enabled: Boolean) : Intent
    }

    /**
     * Cross-feature coordination state only.
     * Child-private state is owned by respective child components.
     */
    data class State(
        val selectedFeatureInfoWindow: MapScreenComponent.FeatureInfoWindow? = null,
        val viewportPendingCommand: MapViewportCommand? = null,
        val locationPendingViewportCommand: MapViewportCommand? = null,
        val rulerPendingViewportCommand: MapViewportCommand? = null,
        val isRulerEnabled: Boolean = false,
    ) {
        val pendingViewportCommand: MapViewportCommand?
            get() = viewportPendingCommand ?: locationPendingViewportCommand ?: rulerPendingViewportCommand

        val currentViewportCommandSource: ViewportCommandSource?
            get() = when {
                viewportPendingCommand != null -> ViewportCommandSource.VIEWPORT
                locationPendingViewportCommand != null -> ViewportCommandSource.LOCATION
                rulerPendingViewportCommand != null -> ViewportCommandSource.RULER
                else -> null
            }

        fun toModel(): MapScreenComponent.Model = MapScreenComponent.Model(
            isRulerEnabled = isRulerEnabled,
            pendingViewportCommand = pendingViewportCommand,
            selectedFeatureInfoWindow = selectedFeatureInfoWindow,
        )
    }

    sealed interface Label {
        data class ViewportCommandRequested(val command: MapViewportCommand) : Label
        data class LocationRequestIssued(val request: MapLocationRequest) : Label
        data object DismissToolsMenu : Label
        data object DismissViewportMenu : Label
        data object CenterMarkerMenuOpenRequested : Label
    }

    enum class OverlayTarget(
        val dismissToolsMenu: Boolean,
        val dismissViewportMenu: Boolean,
        val clearFeatureInfoWindow: Boolean,
    ) {
        TOOLS_OVERLAY(
            dismissToolsMenu = false,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
        CENTER_MARKER_MENU(
            dismissToolsMenu = true,
            dismissViewportMenu = false,
            clearFeatureInfoWindow = true,
        ),
        DRAWING_OVERLAY(
            dismissToolsMenu = true,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
        VIEWPORT_EXCLUSIVE_ACTION(
            dismissToolsMenu = false,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
        FEATURE_SELECTION(
            dismissToolsMenu = true,
            dismissViewportMenu = true,
            clearFeatureInfoWindow = false,
        ),
    }

    enum class ViewportCommandSource {
        VIEWPORT,
        LOCATION,
        RULER,
    }
}
