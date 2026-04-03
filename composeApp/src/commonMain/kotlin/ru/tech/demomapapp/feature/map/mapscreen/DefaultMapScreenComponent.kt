package ru.tech.demomapapp.feature.map.mapscreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStore
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreFactory
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreHolder

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    initialModel: MapScreenComponent.Model = MapScreenComponent.Model(),
    private val mapRouterStoreFactory: MapRouterStoreFactory = MapRouterStoreFactory(),
) : ComponentContext by componentContext {
    private val routerHolder = instanceKeeper.getOrCreate(key = MAP_ROUTER_STORE_HOLDER_KEY) {
        MapRouterStoreHolder(
            mapRouterStoreFactory = mapRouterStoreFactory,
            initialModel = initialModel,
        )
    }

    val model: Value<MapScreenComponent.Model> = routerHolder.model

    fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) {
        routerHolder.accept(
            MapRouterStore.Intent.FeatureClicked(
                featureKey = featureKey,
                featureType = featureType,
                anchor = anchor,
            ),
        )
    }

    fun onFeatureInfoWindowDismiss() {
        routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)
    }

    fun onToolsStateUpdated(state: MapRouterStore.ChildState.Tools) {
        routerHolder.accept(MapRouterStore.Intent.ToolsStateUpdated(state))
    }

    fun onDrawingStateUpdated(state: MapRouterStore.ChildState.Drawing) {
        routerHolder.accept(MapRouterStore.Intent.DrawingStateUpdated(state))
    }

    fun onLocationStateUpdated(state: MapRouterStore.ChildState.Location) {
        routerHolder.accept(MapRouterStore.Intent.LocationStateUpdated(state))
    }

    fun onRulerStateUpdated(state: MapRouterStore.ChildState.Ruler) {
        routerHolder.accept(MapRouterStore.Intent.RulerStateUpdated(state))
    }

    fun onViewportStateUpdated(state: MapRouterStore.ChildState.Viewport) {
        routerHolder.accept(MapRouterStore.Intent.ViewportStateUpdated(state))
    }

    fun onCenterMarkerStateUpdated(state: MapRouterStore.ChildState.CenterMarker) {
        routerHolder.accept(MapRouterStore.Intent.CenterMarkerStateUpdated(state))
    }

    fun onViewportCommandUpdated(
        source: MapRouterStore.ViewportCommandSource,
        command: ru.tech.demomapapp.feature.map.api.MapViewportCommand?,
    ) {
        routerHolder.accept(MapRouterStore.Intent.ViewportCommandUpdated(source = source, command = command))
    }

    fun currentViewportCommandSource(): MapRouterStore.ViewportCommandSource? = when {
        routerHolder.state.viewportPendingCommand != null -> MapRouterStore.ViewportCommandSource.VIEWPORT
        routerHolder.state.locationPendingViewportCommand != null -> MapRouterStore.ViewportCommandSource.LOCATION
        routerHolder.state.rulerPendingViewportCommand != null -> MapRouterStore.ViewportCommandSource.RULER
        else -> null
    }

    fun onViewportCommandConsumed(source: MapRouterStore.ViewportCommandSource) {
        routerHolder.accept(MapRouterStore.Intent.ViewportCommandConsumed(source))
    }

    fun hasSelectedFeatureInfoWindow(): Boolean = routerHolder.model.value.selectedFeatureInfoWindow != null

    fun dismissFeatureInfoWindow() {
        routerHolder.accept(MapRouterStore.Intent.FeatureInfoWindowDismissed)
    }

    private companion object {
        const val MAP_ROUTER_STORE_HOLDER_KEY = "DefaultMapScreenComponent.mapRouterStoreHolder"
    }
}
