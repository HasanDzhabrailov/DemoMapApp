package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultRulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.DefaultMapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.DefaultMapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.DefaultRulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.MapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.MapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.ShapeDrawingDraftUpdater
import ru.tech.demomapapp.feature.map.impl.TimeProvider

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val createMapPointUseCase: CreateMapPointUseCase,
    private val createMapLineUseCase: CreateMapLineUseCase,
    private val createMapPolygonUseCase: CreateMapPolygonUseCase,
    private val shapeDrawingDraftUpdater: ShapeDrawingDraftUpdater,
    private val timeProvider: TimeProvider,
    private val featureIdProvider: FeatureIdProvider,
    private val featureSelectionResolver: MapFeatureSelectionResolver = DefaultMapFeatureSelectionResolver(),
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
    private val rulerMeasurementCalculator: RulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
) {
    fun create(initialState: MapStore.State = MapStore.State()): MapStore =
        object : MapStore, Store<MapStore.Intent, MapStore.State, MapStore.Label> by storeFactory.create(
            name = "MapStore",
            initialState = initialState,
            bootstrapper = null,
            executorFactory = {
                MapStoreExecutor(
                    createMapPointUseCase = createMapPointUseCase,
                    createMapLineUseCase = createMapLineUseCase,
                    createMapPolygonUseCase = createMapPolygonUseCase,
                    timeProvider = timeProvider,
                    featureIdProvider = featureIdProvider,
                    featureSelectionResolver = featureSelectionResolver,
                    featureInfoWindowStateMapper = featureInfoWindowStateMapper,
                    rulerMeasurementCalculator = rulerMeasurementCalculator,
                    rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
                )
            },
            reducer = MapStoreReducer(shapeDrawingDraftUpdater),
        ) {}
}
