package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.store.MapStoreFactory
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreHolder
import ru.tech.demomapapp.feature.map.impl.store.toStoreAnchor
import ru.tech.demomapapp.feature.map.impl.store.toStoreFeatureType

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
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
    private val mapStoreFactory: MapStoreFactory = MapStoreFactory(
        createMapPointUseCase = createMapPointUseCase,
        createMapLineUseCase = createMapLineUseCase,
        createMapPolygonUseCase = createMapPolygonUseCase,
        shapeDrawingDraftUpdater = shapeDrawingDraftUpdater,
        timeProvider = timeProvider,
        featureIdProvider = featureIdProvider,
        rulerMeasurementCalculator = rulerMeasurementCalculator,
        rulerInfoWindowStateFormatter = rulerInfoWindowStateFormatter,
    ),
) : MapScreenComponent, ComponentContext by componentContext {
    private val mapStoreHolder = instanceKeeper.getOrCreate(key = MAP_STORE_HOLDER_KEY) {
        MapStoreHolder(
            mapStoreFactory = mapStoreFactory,
            initialModel = defaultModel(),
        )
    }
    override val model: Value<MapScreenComponent.Model> = mapStoreHolder.model

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        acceptIntent(MapStore.Intent.Viewport.CameraIdle(snapshot))
    }

    override fun onMapToolsClick() {
        acceptIntent(MapStore.Intent.Tools.MapToolsClicked)
    }

    override fun onMapToolsDismiss() {
        acceptIntent(MapStore.Intent.Tools.MapToolsDismissed)
    }

    override fun onZoomInClick() {
        acceptIntent(MapStore.Intent.Viewport.ZoomInClicked)
    }

    override fun onZoomOutClick() {
        acceptIntent(MapStore.Intent.Viewport.ZoomOutClicked)
    }

    override fun onAvailableMapsClick() {
        acceptIntent(MapStore.Intent.Tools.AvailableMapsClicked)
    }

    override fun onMapsOnScreenClick() {
        acceptIntent(MapStore.Intent.Tools.MapsOnScreenClicked)
    }

    override fun onGpsToggle() {
        acceptIntent(MapStore.Intent.Location.GpsToggled)
    }

    override fun onMyLocationClick() {
        acceptIntent(MapStore.Intent.Location.MyLocationClicked)
    }

    override fun onCurrentLocationFocusClick() {
        acceptIntent(MapStore.Intent.Location.CurrentLocationFocusClicked)
    }

    override fun onLocationRequestConsumed() {
        mapStoreHolder.consumeLocationRequest()
    }

    override fun onLocationResult(result: LocationRequestResult) {
        acceptIntent(MapStore.Intent.Location.LocationResultReceived(result))
    }

    override fun onRulerToggle() {
        acceptIntent(MapStore.Intent.Ruler.Toggled)
    }

    override fun onViewportCommandConsumed() {
        mapStoreHolder.consumeViewportCommand()
    }

    override fun onCenterMarkerClick() {
        acceptIntent(MapStore.Intent.CenterMarker.Clicked)
    }

    override fun onCenterMarkerMenuDismiss() {
        acceptIntent(MapStore.Intent.CenterMarker.MenuDismissed)
    }

    override fun onCreatePointClick() {
        acceptIntent(MapStore.Intent.CreatePoint.Clicked)
    }

    override fun onCreateLineClick() {
        acceptIntent(MapStore.Intent.Drawing.CreateLineClicked)
    }

    override fun onCreatePolygonClick() {
        acceptIntent(MapStore.Intent.Drawing.CreatePolygonClicked)
    }

    override fun onCreatePointLatitudeChange(value: String) {
        acceptIntent(MapStore.Intent.CreatePoint.LatitudeChanged(value))
    }

    override fun onCreatePointLongitudeChange(value: String) {
        acceptIntent(MapStore.Intent.CreatePoint.LongitudeChanged(value))
    }

    override fun onCreatePointTitleChange(value: String) {
        acceptIntent(MapStore.Intent.CreatePoint.TitleChanged(value))
    }

    override fun onCreatePointConfirm() {
        acceptIntent(MapStore.Intent.CreatePoint.Confirmed)
    }

    override fun onCreatePointSheetDismiss() {
        acceptIntent(MapStore.Intent.CreatePoint.SheetDismissed)
    }

    override fun onDrawingAddPositionClick() {
        acceptIntent(MapStore.Intent.Drawing.AddPositionClicked)
    }

    override fun onDrawingRemoveLastPositionClick() {
        acceptIntent(MapStore.Intent.Drawing.RemoveLastPositionClicked)
    }

    override fun onDrawingDetailsClick() {
        acceptIntent(MapStore.Intent.Drawing.DetailsClicked)
    }

    override fun onDrawingDismiss() {
        acceptIntent(MapStore.Intent.Drawing.Dismissed)
    }

    override fun onCreateShapeTitleChange(value: String) {
        acceptIntent(MapStore.Intent.Drawing.TitleChanged(value))
    }

    override fun onCreateShapeConfirm() {
        acceptIntent(MapStore.Intent.Drawing.Confirmed)
    }

    override fun onCreateShapeSheetDismiss() {
        acceptIntent(MapStore.Intent.Drawing.ShapeSheetDismissed)
    }

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) {
        acceptIntent(
            MapStore.Intent.FeatureSelection.FeatureClicked(
                featureKey = featureKey,
                featureType = featureType.toStoreFeatureType(),
                anchor = anchor.toStoreAnchor(),
            ),
        )
        val model = currentModel()
        val feature = featureSelectionResolver.resolve(model.mapState, featureKey, featureType) ?: return
        setModel(model.copy(
            isMapToolsMenuVisible = false,
            isCenterMarkerMenuVisible = false,
            selectedFeatureInfoWindow = featureInfoWindowStateMapper.map(feature, anchor),
        ))
    }

    override fun onFeatureInfoWindowDismiss() {
        acceptIntent(MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed)
    }

    private fun defaultModel(): MapScreenComponent.Model =
        MapScreenComponent.Model()

    private fun currentModel(): MapScreenComponent.Model = model.value

    private fun acceptIntent(intent: MapStore.Intent) {
        mapStoreHolder.accept(intent)
    }

    private fun setModel(model: MapScreenComponent.Model) {
        mapStoreHolder.updateModel(model)
    }

    private companion object {
        const val MAP_STORE_HOLDER_KEY = "DefaultMapScreenComponent.mapStoreHolder"
    }
}

internal fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

internal class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = platformCurrentTimeMillis()
}

internal fun interface FeatureIdProvider {
    fun nextId(): String
}

internal class UuidFeatureIdProvider : FeatureIdProvider {
    override fun nextId(): String = generateMapPointId()
}
