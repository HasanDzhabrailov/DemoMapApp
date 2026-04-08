package ru.tech.demomapapp.feature.map.router

import com.arkivanov.mvikotlin.core.store.Executor

internal class MapRouterExecutor : Executor<
    MapRouterStore.Intent,
    MapRouterStore.Action,
    MapRouterStore.State,
    MapRouterMessage,
    MapRouterStore.Label,
    > {

    constructor() : this(
        featureSelectionResolver = DefaultMapFeatureSelectionResolver(),
        featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
    )

    internal constructor(
        featureSelectionResolver: MapFeatureSelectionResolver,
        featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper,
    ) {
        this.featureSelectionResolver = featureSelectionResolver
        this.featureInfoWindowStateMapper = featureInfoWindowStateMapper
    }

    private val featureSelectionResolver: MapFeatureSelectionResolver
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper

    private lateinit var callbacks: Executor.Callbacks<
        MapRouterStore.State,
        MapRouterMessage,
        MapRouterStore.Action,
        MapRouterStore.Label,
        >

    override fun init(
        callbacks: Executor.Callbacks<
            MapRouterStore.State,
            MapRouterMessage,
            MapRouterStore.Action,
            MapRouterStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: MapRouterStore.Intent) {
        when (intent) {
            is MapRouterStore.Intent.OverlayInteractionRequested -> {
                callbacks.onMessage(MapRouterMessage.OverlayInteractionProcessed(intent.target))
                // Always publish dismiss labels - child components handle their own state
                if (intent.target.dismissToolsMenu) {
                    callbacks.onLabel(MapRouterStore.Label.DismissToolsMenu)
                }
                if (intent.target.dismissViewportMenu) {
                    callbacks.onLabel(MapRouterStore.Label.DismissViewportMenu)
                }
            }

            MapRouterStore.Intent.ToolsMenuDismissRequested -> {
                callbacks.onLabel(MapRouterStore.Label.DismissToolsMenu)
            }

            MapRouterStore.Intent.ViewportMenuDismissRequested -> {
                callbacks.onLabel(MapRouterStore.Label.DismissViewportMenu)
            }

            MapRouterStore.Intent.CenterMarkerClicked -> {
                // Center marker click is handled by viewport child component
                // Parent only coordinates cross-feature concerns
                callbacks.onLabel(MapRouterStore.Label.CenterMarkerMenuOpenRequested)
            }

            is MapRouterStore.Intent.FeatureClicked -> {
                // Feature selection requires render data from child components
                // This is passed via intent parameters now
                val selectedFeature = featureSelectionResolver.resolve(
                    points = intent.points,
                    lines = intent.lines,
                    polygons = intent.polygons,
                    featureKey = intent.featureKey,
                    featureType = intent.featureType,
                )
                callbacks.onMessage(
                    MapRouterMessage.FeatureInfoWindowUpdated(
                        infoWindow = selectedFeature?.let {
                            featureInfoWindowStateMapper.map(it, intent.anchor)
                        },
                    ),
                )
            }

            MapRouterStore.Intent.FeatureInfoWindowDismissed -> {
                callbacks.onMessage(MapRouterMessage.FeatureInfoWindowUpdated(infoWindow = null))
            }

            is MapRouterStore.Intent.ViewportCommandUpdated -> {
                callbacks.onMessage(MapRouterMessage.ViewportCommandUpdated(intent.source, intent.command))
            }

            MapRouterStore.Intent.ViewportCommandConsumed -> {
                callbacks.onMessage(MapRouterMessage.ViewportCommandConsumed)
            }

            is MapRouterStore.Intent.RulerEnabledUpdated -> {
                callbacks.onMessage(MapRouterMessage.RulerEnabledUpdated(intent.enabled))
            }
        }
    }

    override fun executeAction(action: MapRouterStore.Action) = Unit

    override fun dispose() = Unit
}
