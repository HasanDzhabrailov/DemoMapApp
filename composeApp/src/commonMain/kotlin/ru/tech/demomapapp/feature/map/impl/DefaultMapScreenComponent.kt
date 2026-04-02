package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.impl.location.DefaultLocationComponent
import ru.tech.demomapapp.feature.map.impl.location.LocationComponent
import ru.tech.demomapapp.feature.map.impl.location.LocationStoreFactory
import ru.tech.demomapapp.feature.map.impl.ruler.DefaultRulerComponent
import ru.tech.demomapapp.feature.map.impl.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.impl.ruler.RulerStoreFactory
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreFactory
import ru.tech.demomapapp.feature.map.impl.store.MapStoreHolder
import ru.tech.demomapapp.feature.map.impl.store.toStoreAnchor
import ru.tech.demomapapp.feature.map.impl.store.toStoreFeatureType

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    private val mapStoreFactory: MapStoreFactory = MapStoreFactory(),
    private val locationStoreFactory: LocationStoreFactory = LocationStoreFactory(),
    private val rulerStoreFactory: RulerStoreFactory = RulerStoreFactory(),
) : MapScreenComponent, ComponentContext by componentContext {
    private val holder = instanceKeeper.getOrCreate(key = MAP_STORE_HOLDER_KEY) {
        MapStoreHolder(
            mapStoreFactory = mapStoreFactory,
            initialModel = MapScreenComponent.Model(),
        )
    }
    private val rulerComponent: RulerComponent = DefaultRulerComponent(
        componentContext = componentContext,
        rulerStoreFactory = rulerStoreFactory,
        output = RulerComponent.Output(::handleRulerViewportCommand),
    )
    private val locationComponent: LocationComponent = DefaultLocationComponent(
        componentContext = componentContext,
        locationStoreFactory = locationStoreFactory,
        output = object : LocationComponent.Output {
            override fun onLocationUpdated(location: MapLocationMarker?) {
                handleLocationUpdated(location)
            }

            override fun onViewportCommandRequested(command: MapViewportCommand) {
                handleLocationViewportCommand(command)
            }

            override fun onLocationRequestIssued(request: MapLocationRequest) = Unit
        },
    )
    private val mutableModel = MutableValue(mergeModels())
    private var pendingLocationViewportCommand: MapViewportCommand? = null
    private var pendingRulerViewportCommand: MapViewportCommand? = null
    private var syncedRulerLocation = locationComponent.model.value.currentMarker
    private var syncedRulerSnapshot = holder.model.value.lastCameraSnapshot

    override val model: Value<MapScreenComponent.Model> = mutableModel

    init {
        syncRulerState()
        refreshModel()
    }

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        holder.accept(MapStore.Intent.Viewport.CameraIdle(snapshot))
        locationComponent.onCameraSnapshotReceived(snapshot)
        syncRulerState()
        refreshModel()
    }

    override fun onMapToolsClick() = acceptMapIntent(MapStore.Intent.Tools.MapToolsClicked)
    override fun onMapToolsDismiss() = acceptMapIntent(MapStore.Intent.Tools.MapToolsDismissed)
    override fun onZoomInClick() = acceptMapIntent(MapStore.Intent.Viewport.ZoomInClicked)
    override fun onZoomOutClick() = acceptMapIntent(MapStore.Intent.Viewport.ZoomOutClicked)
    override fun onAvailableMapsClick() = acceptMapIntent(MapStore.Intent.Tools.AvailableMapsClicked)
    override fun onAvailableMapsDismiss() = acceptMapIntent(MapStore.Intent.Tools.AvailableMapsDismissed)
    override fun onAvailableMapSelect(mapId: String) = acceptMapIntent(
        MapStore.Intent.Tools.AvailableMapSelected(mapId),
    )
    override fun onAvailableMapConfirm() = acceptMapIntent(MapStore.Intent.Tools.AvailableMapConfirmed)
    override fun onAvailableMapSelectionDismiss() = acceptMapIntent(
        MapStore.Intent.Tools.AvailableMapSelectionDismissed,
    )
    override fun onMapsOnScreenClick() = acceptMapIntent(MapStore.Intent.Tools.MapsOnScreenClicked)
    override fun onMapsOnScreenDismiss() = acceptMapIntent(MapStore.Intent.Tools.MapsOnScreenDismissed)
    override fun onMapLayerActionsClick(layerId: String) = acceptMapIntent(
        MapStore.Intent.Tools.OverlayLayerActionsClicked(layerId),
    )
    override fun onMapLayerActionsDismiss() = acceptMapIntent(MapStore.Intent.Tools.OverlayLayerActionsDismissed)
    override fun onMoveLayerUpClick() = acceptMapIntent(MapStore.Intent.Tools.OverlayLayerMoveUpClicked)
    override fun onMoveLayerDownClick() = acceptMapIntent(MapStore.Intent.Tools.OverlayLayerMoveDownClicked)
    override fun onRemoveLayerClick() = acceptMapIntent(MapStore.Intent.Tools.OverlayLayerRemoveClicked)
    override fun onLayerOpacityClick() = acceptMapIntent(MapStore.Intent.Tools.OverlayLayerOpacityClicked)
    override fun onLayerOpacityChange(value: Float) = acceptMapIntent(
        MapStore.Intent.Tools.OverlayLayerOpacityChanged(value),
    )
    override fun onLayerOpacityDismiss() = acceptMapIntent(MapStore.Intent.Tools.OverlayLayerOpacityDismissed)
    override fun onGpsToggle() = acceptLocationUpdate(locationComponent::onGpsToggle)
    override fun onMyLocationClick() = acceptLocationUpdate(locationComponent::onMyLocationClick)
    override fun onCurrentLocationFocusClick() = acceptLocationUpdate(locationComponent::onCurrentLocationFocusClick)
    override fun onLocationRequestConsumed() = acceptLocationUpdate(locationComponent::onLocationRequestConsumed)
    override fun onLocationResult(result: LocationRequestResult) = acceptLocationUpdate {
        locationComponent.onLocationResult(result)
    }
    override fun onRulerToggle() {
        rulerComponent.onToggleClicked()
        syncRulerState()
        refreshModel()
    }
    override fun onViewportCommandConsumed() {
        if (holder.model.value.pendingViewportCommand != null) {
            holder.accept(MapStore.Intent.Viewport.ViewportCommandConsumed)
        } else if (pendingLocationViewportCommand != null) {
            pendingLocationViewportCommand = null
        } else {
            pendingRulerViewportCommand = null
        }
        refreshModel()
    }
    override fun onCenterMarkerClick() = acceptMapIntent(MapStore.Intent.CenterMarker.Clicked)
    override fun onCenterMarkerMenuDismiss() = acceptMapIntent(MapStore.Intent.CenterMarker.MenuDismissed)
    override fun onCreatePointClick() = acceptMapIntent(MapStore.Intent.CreatePoint.Clicked)
    override fun onCreateLineClick() = acceptMapIntent(MapStore.Intent.Drawing.CreateLineClicked)
    override fun onCreatePolygonClick() = acceptMapIntent(MapStore.Intent.Drawing.CreatePolygonClicked)
    override fun onCreatePointLatitudeChange(value: String) = acceptMapIntent(
        MapStore.Intent.CreatePoint.LatitudeChanged(value),
    )
    override fun onCreatePointLongitudeChange(value: String) = acceptMapIntent(
        MapStore.Intent.CreatePoint.LongitudeChanged(value),
    )
    override fun onCreatePointTitleChange(value: String) = acceptMapIntent(
        MapStore.Intent.CreatePoint.TitleChanged(value),
    )
    override fun onCreatePointConfirm() = acceptMapIntent(MapStore.Intent.CreatePoint.Confirmed)
    override fun onCreatePointSheetDismiss() = acceptMapIntent(MapStore.Intent.CreatePoint.SheetDismissed)
    override fun onDrawingAddPositionClick() = acceptMapIntent(MapStore.Intent.Drawing.AddPositionClicked)
    override fun onDrawingRemoveLastPositionClick() = acceptMapIntent(MapStore.Intent.Drawing.RemoveLastPositionClicked)
    override fun onDrawingDetailsClick() = acceptMapIntent(MapStore.Intent.Drawing.DetailsClicked)
    override fun onDrawingDismiss() = acceptMapIntent(MapStore.Intent.Drawing.Dismissed)
    override fun onCreateShapeTitleChange(value: String) = acceptMapIntent(MapStore.Intent.Drawing.TitleChanged(value))
    override fun onCreateShapeConfirm() = acceptMapIntent(MapStore.Intent.Drawing.Confirmed)
    override fun onCreateShapeSheetDismiss() = acceptMapIntent(MapStore.Intent.Drawing.ShapeSheetDismissed)

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) = acceptMapIntent(
        MapStore.Intent.FeatureSelection.FeatureClicked(
            featureKey = featureKey,
            featureType = featureType.toStoreFeatureType(),
            anchor = anchor.toStoreAnchor(),
        ),
    )

    override fun onFeatureInfoWindowDismiss() = acceptMapIntent(
        MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed,
    )

    private fun acceptMapIntent(intent: MapStore.Intent) {
        holder.accept(intent)
        syncRulerState()
        refreshModel()
    }

    private fun acceptLocationUpdate(action: () -> Unit) {
        action()
        syncRulerState()
        refreshModel()
    }

    private fun syncRulerState() {
        val mapModel = holder.model.value
        if (mapModel.lastCameraSnapshot != syncedRulerSnapshot) {
            mapModel.lastCameraSnapshot?.let(rulerComponent::onCameraSnapshotReceived)
            syncedRulerSnapshot = mapModel.lastCameraSnapshot
        }
        val locationMarker = locationComponent.model.value.currentMarker
        if (locationMarker != syncedRulerLocation) {
            rulerComponent.onLocationUpdated(locationMarker)
            syncedRulerLocation = locationMarker
        }
    }

    private fun refreshModel() {
        if (holder.model.value.pendingViewportCommand != null) {
            pendingLocationViewportCommand = null
            pendingRulerViewportCommand = null
        } else if (pendingLocationViewportCommand != null) {
            pendingRulerViewportCommand = null
        }
        mutableModel.value = mergeModels()
    }

    private fun mergeModels(): MapScreenComponent.Model {
        val mapModel = holder.model.value
        val locationModel = locationComponent.model.value
        val rulerModel = rulerComponent.model.value
        val pendingViewportCommand =
            mapModel.pendingViewportCommand ?: pendingLocationViewportCommand ?: pendingRulerViewportCommand

        return mapModel.copy(
            myLocationMode = locationModel.mode,
            currentLocationMarker = locationModel.currentMarker,
            pendingLocationRequest = locationModel.pendingRequest,
            isRulerEnabled = rulerModel.isEnabled,
            rulerMeasurement = rulerModel.measurement,
            rulerInfoWindow = rulerModel.infoWindow,
            pendingViewportCommand = pendingViewportCommand,
        )
    }

    private fun handleLocationUpdated(location: MapLocationMarker?) {
        if (location != syncedRulerLocation) {
            rulerComponent.onLocationUpdated(location)
            syncedRulerLocation = location
        }
    }

    private fun handleLocationViewportCommand(command: MapViewportCommand) {
        pendingLocationViewportCommand = command
    }

    private fun handleRulerViewportCommand(command: MapViewportCommand) {
        pendingRulerViewportCommand = command
        refreshModel()
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
