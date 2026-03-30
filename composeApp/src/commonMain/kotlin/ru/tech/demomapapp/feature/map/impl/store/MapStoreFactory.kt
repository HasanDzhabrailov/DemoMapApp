package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.tech.demomapapp.feature.map.impl.CreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultRulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.DefaultShapeDrawingDraftUpdater
import ru.tech.demomapapp.feature.map.impl.DefaultMapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.DefaultMapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.DefaultRulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.DefaultCreateMapLineUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultCreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.DefaultCreateMapPolygonUseCase
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.MapFeatureInfoWindowStateMapper
import ru.tech.demomapapp.feature.map.impl.MapFeatureSelectionResolver
import ru.tech.demomapapp.feature.map.impl.RulerInfoWindowStateFormatter
import ru.tech.demomapapp.feature.map.impl.RulerMeasurementCalculator
import ru.tech.demomapapp.feature.map.impl.ShapeDrawingDraftUpdater
import ru.tech.demomapapp.feature.map.impl.TimeProvider
import ru.tech.demomapapp.feature.map.impl.SystemTimeProvider
import ru.tech.demomapapp.feature.map.impl.UuidFeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.store.handler.CreatePointHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.DrawingHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.FeatureClickHandler
import ru.tech.demomapapp.feature.map.impl.store.handler.LocationHandler

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val createMapPointUseCase: CreateMapPointUseCase = DefaultCreateMapPointUseCase(),
    private val createMapLineUseCase: CreateMapLineUseCase = DefaultCreateMapLineUseCase(),
    private val createMapPolygonUseCase: CreateMapPolygonUseCase = DefaultCreateMapPolygonUseCase(),
    private val shapeDrawingDraftUpdater: ShapeDrawingDraftUpdater = DefaultShapeDrawingDraftUpdater(),
    private val timeProvider: TimeProvider = SystemTimeProvider(),
    private val featureIdProvider: FeatureIdProvider = UuidFeatureIdProvider(),
    private val featureSelectionResolver: MapFeatureSelectionResolver = DefaultMapFeatureSelectionResolver(),
    private val featureInfoWindowStateMapper: MapFeatureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(),
    private val rulerMeasurementCalculator: RulerMeasurementCalculator = DefaultRulerMeasurementCalculator,
    private val rulerInfoWindowStateFormatter: RulerInfoWindowStateFormatter = DefaultRulerInfoWindowStateFormatter,
) {
    private val createPointHandler: CreatePointHandler by lazy {
        CreatePointHandler(
            createMapPointUseCase = createMapPointUseCase,
            timeProvider = timeProvider,
            featureIdProvider = featureIdProvider,
        )
    }

    private val drawingHandler: DrawingHandler by lazy {
        DrawingHandler(
            createMapLineUseCase = createMapLineUseCase,
            createMapPolygonUseCase = createMapPolygonUseCase,
            timeProvider = timeProvider,
            featureIdProvider = featureIdProvider,
        )
    }

    private val locationHandler: LocationHandler by lazy {
        LocationHandler()
    }

    private val featureClickHandler: FeatureClickHandler by lazy {
        FeatureClickHandler(
            featureSelectionResolver = featureSelectionResolver,
            featureInfoWindowStateMapper = featureInfoWindowStateMapper,
        )
    }

    fun create(initialState: MapStore.State = MapStore.State()): MapStore =
        object : MapStore, Store<MapStore.Intent, MapStore.State, MapStore.Label> by storeFactory.create(
            name = "MapStore",
            initialState = initialState,
            bootstrapper = null,
            executorFactory = {
                MapStoreExecutor(
                    createPointHandler = createPointHandler,
                    drawingHandler = drawingHandler,
                    locationHandler = locationHandler,
                    featureClickHandler = featureClickHandler,
                    rulerMeasurementCalculator = rulerMeasurementCalculator,
                    rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
                )
            },
            reducer = MapStoreReducer(shapeDrawingDraftUpdater),
        ) {}
}