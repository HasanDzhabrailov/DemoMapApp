package ru.tech.demomapapp.feature.map.impl.router

import com.arkivanov.mvikotlin.core.store.Executor
internal class MapRouterExecutor : Executor<
    MapRouterStore.Intent,
    Nothing,
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
        Nothing,
        MapRouterStore.Label,
        >

    override fun init(
        callbacks: Executor.Callbacks<
            MapRouterStore.State,
            MapRouterMessage,
            Nothing,
            MapRouterStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: MapRouterStore.Intent) {
        when (intent) {
            is MapRouterStore.Intent.ViewportStateUpdated -> {
                callbacks.onMessage(MapRouterMessage.ViewportStateUpdated(intent.state))
            }

            is MapRouterStore.Intent.ToolsStateUpdated -> {
                callbacks.onMessage(MapRouterMessage.ToolsStateUpdated(intent.state))
            }

            is MapRouterStore.Intent.LocationStateUpdated -> {
                callbacks.onMessage(MapRouterMessage.LocationStateUpdated(intent.state))
                intent.state.activeLocationRequest?.let { request ->
                    callbacks.onLabel(MapRouterStore.Label.LocationRequestIssued(request))
                }
            }

            is MapRouterStore.Intent.RulerStateUpdated -> {
                callbacks.onMessage(MapRouterMessage.RulerStateUpdated(intent.state))
            }

            is MapRouterStore.Intent.CenterMarkerStateUpdated -> {
                callbacks.onMessage(MapRouterMessage.CenterMarkerStateUpdated(intent.state))
            }

            is MapRouterStore.Intent.DrawingStateUpdated -> {
                callbacks.onMessage(MapRouterMessage.DrawingStateUpdated(intent.state))
            }

            is MapRouterStore.Intent.FeatureClicked -> {
                val selectedFeature = featureSelectionResolver.resolve(
                    mapState = callbacks.state.mapState,
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

            is MapRouterStore.Intent.ViewportCommandConsumed -> {
                callbacks.onMessage(MapRouterMessage.ViewportCommandUpdated(intent.source, command = null))
            }
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit
}
