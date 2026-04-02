package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
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
import ru.tech.demomapapp.feature.map.impl.tools.DefaultToolsComponent
import ru.tech.demomapapp.feature.map.impl.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.impl.tools.ToolsStoreFactory
import ru.tech.demomapapp.feature.map.impl.viewport.DefaultViewportComponent
import ru.tech.demomapapp.feature.map.impl.viewport.ViewportComponent
import ru.tech.demomapapp.feature.map.impl.viewport.ViewportStoreFactory

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    initialModel: MapScreenComponent.Model = MapScreenComponent.Model(),
    private val mapStoreFactory: MapStoreFactory = MapStoreFactory(),
    private val toolsStoreFactory: ToolsStoreFactory = ToolsStoreFactory(),
    private val locationStoreFactory: LocationStoreFactory = LocationStoreFactory(),
    private val rulerStoreFactory: RulerStoreFactory = RulerStoreFactory(),
    private val viewportStoreFactory: ViewportStoreFactory = ViewportStoreFactory(),
) : MapScreenComponent, ComponentContext by componentContext {
    private val holder = instanceKeeper.getOrCreate(key = MAP_STORE_HOLDER_KEY) {
        MapStoreHolder(
            mapStoreFactory = mapStoreFactory,
            initialModel = initialModel,
        )
    }
    private val toolsComponent: ToolsComponent = DefaultToolsComponent(
        componentContext = componentContext,
        toolsStoreFactory = toolsStoreFactory,
        initialModel = initialModel,
        output = ToolsComponent.Output(::handleToolsLayersChanged),
    )
    private val rulerComponent: RulerComponent = DefaultRulerComponent(
        componentContext = componentContext,
        rulerStoreFactory = rulerStoreFactory,
        output = RulerComponent.Output(::handleRulerViewportCommand),
    )
    private val viewportComponent: ViewportComponent = DefaultViewportComponent(
        componentContext = componentContext,
        viewportStoreFactory = viewportStoreFactory,
        output = ViewportComponent.Output(::handleViewportCommandRequested),
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
    private var pendingViewportCommand: MapViewportCommand? = null
    private var pendingRulerViewportCommand: MapViewportCommand? = null
    private var syncedRulerLocation = locationComponent.model.value.currentMarker
    private var syncedRulerSnapshot = viewportComponent.model.value.cameraSnapshot

    override val model: Value<MapScreenComponent.Model> = mutableModel

    init {
        syncRulerState()
        refreshModel()
    }

    override fun onCameraIdle(snapshot: MapCameraSnapshot) {
        viewportComponent.onCameraIdle(snapshot)
        holder.accept(MapStore.Intent.CameraIdle(snapshot))
        locationComponent.onCameraSnapshotReceived(snapshot)
        syncRulerState()
        refreshModel()
    }

    override fun onMapToolsClick() = acceptToolsUpdate(
        clearSelectedFeatureInfoWindow = true,
        action = toolsComponent::onMapToolsClick,
    )
    override fun onMapToolsDismiss() = acceptToolsUpdate(action = toolsComponent::onMapToolsDismiss)
    override fun onZoomInClick() = acceptViewportUpdate(viewportComponent::onZoomInClick)
    override fun onZoomOutClick() = acceptViewportUpdate(viewportComponent::onZoomOutClick)
    override fun onAvailableMapsClick() = acceptToolsUpdate(
        clearSelectedFeatureInfoWindow = true,
        action = toolsComponent::onAvailableMapsClick,
    )
    override fun onAvailableMapsDismiss() = acceptToolsUpdate(
        action = toolsComponent::onAvailableMapsDismiss,
    )
    override fun onAvailableMapSelect(mapId: String) = acceptToolsUpdate {
        toolsComponent.onAvailableMapSelect(mapId)
    }
    override fun onAvailableMapConfirm() = acceptToolsUpdate(
        action = toolsComponent::onAvailableMapConfirm,
    )
    override fun onAvailableMapSelectionDismiss() = acceptToolsUpdate(
        action = toolsComponent::onAvailableMapSelectionDismiss,
    )
    override fun onMapsOnScreenClick() = acceptToolsUpdate(
        clearSelectedFeatureInfoWindow = true,
        action = toolsComponent::onMapsOnScreenClick,
    )
    override fun onMapsOnScreenDismiss() = acceptToolsUpdate(
        action = toolsComponent::onMapsOnScreenDismiss,
    )
    override fun onMapLayerActionsClick(layerId: String) = acceptToolsUpdate {
        toolsComponent.onLayerActionsClick(layerId)
    }
    override fun onMapLayerActionsDismiss() = acceptToolsUpdate(
        action = toolsComponent::onLayerActionsDismiss,
    )
    override fun onMoveLayerUpClick() = acceptToolsUpdate(action = toolsComponent::onMoveLayerUpClick)
    override fun onMoveLayerDownClick() = acceptToolsUpdate(action = toolsComponent::onMoveLayerDownClick)
    override fun onRemoveLayerClick() = acceptToolsUpdate(action = toolsComponent::onRemoveLayerClick)
    override fun onLayerOpacityClick() = acceptToolsUpdate(action = toolsComponent::onLayerOpacityClick)
    override fun onLayerOpacityChange(value: Float) = acceptToolsUpdate {
        toolsComponent.onLayerOpacityChange(value)
    }
    override fun onLayerOpacityDismiss() = acceptToolsUpdate(
        action = toolsComponent::onLayerOpacityDismiss,
    )
    override fun onGpsToggle() = acceptLocationUpdate(locationComponent::onGpsToggle)
    override fun onMyLocationClick() = acceptLocationUpdate(locationComponent::onMyLocationClick)
    override fun onCurrentLocationFocusClick() = acceptLocationUpdate(locationComponent::onCurrentLocationFocusClick)
    override fun onLocationRequestConsumed() = acceptLocationUpdate(locationComponent::onLocationRequestConsumed)
    override fun onLocationResult(result: LocationRequestResult) = acceptLocationUpdate {
        locationComponent.onLocationResult(result)
    }
    override fun onRulerToggle() {
        dismissViewportMenuIfVisible()
        rulerComponent.onToggleClicked()
        syncRulerState()
        refreshModel()
    }
    override fun onViewportCommandConsumed() {
        if (pendingViewportCommand != null) {
            pendingViewportCommand = null
            viewportComponent.onViewportCommandConsumed()
        } else if (pendingLocationViewportCommand != null) {
            pendingLocationViewportCommand = null
        } else {
            pendingRulerViewportCommand = null
        }
        refreshModel()
    }
    override fun onCenterMarkerClick() {
        if (holder.model.value.drawingMode != null) {
            return
        }
        if (toolsComponent.model.value.isMenuVisible) {
            toolsComponent.onMapToolsDismiss()
        }
        if (holder.model.value.selectedFeatureInfoWindow != null) {
            holder.accept(MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed)
        }
        acceptViewportUpdate(viewportComponent::onCenterMarkerClick)
    }
    override fun onCenterMarkerMenuDismiss() = acceptViewportUpdate(viewportComponent::onCenterMarkerMenuDismiss)
    override fun onCreatePointClick() {
        dismissToolsMenuIfVisible()
        acceptMapIntent(MapStore.Intent.CreatePoint.Clicked)
    }
    override fun onCreateLineClick() {
        dismissToolsMenuIfVisible()
        acceptMapIntent(MapStore.Intent.Drawing.CreateLineClicked)
    }
    override fun onCreatePolygonClick() {
        dismissToolsMenuIfVisible()
        acceptMapIntent(MapStore.Intent.Drawing.CreatePolygonClicked)
    }
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
    ) {
        dismissToolsMenuIfVisible()
        acceptMapIntent(
            MapStore.Intent.FeatureSelection.FeatureClicked(
                featureKey = featureKey,
                featureType = featureType.toStoreFeatureType(),
                anchor = anchor.toStoreAnchor(),
            ),
        )
    }

    override fun onFeatureInfoWindowDismiss() = acceptMapIntent(
        MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed,
    )

    private fun acceptMapIntent(intent: MapStore.Intent) {
        if (shouldDismissViewportMenu(intent)) {
            dismissViewportMenuIfVisible()
        }
        holder.accept(intent)
        syncRulerState()
        refreshModel()
    }

    private fun acceptToolsUpdate(clearSelectedFeatureInfoWindow: Boolean = false, action: () -> Unit) {
        dismissViewportMenuIfVisible()
        if (clearSelectedFeatureInfoWindow && holder.model.value.selectedFeatureInfoWindow != null) {
            holder.accept(MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed)
        }
        action()
        refreshModel()
    }

    private fun acceptViewportUpdate(action: () -> Unit) {
        action()
        refreshModel()
    }

    private fun acceptLocationUpdate(action: () -> Unit) {
        action()
        syncRulerState()
        refreshModel()
    }

    private fun syncRulerState() {
        val viewportSnapshot = viewportComponent.model.value.cameraSnapshot
        if (viewportSnapshot != syncedRulerSnapshot) {
            viewportSnapshot?.let(rulerComponent::onCameraSnapshotReceived)
            syncedRulerSnapshot = viewportSnapshot
        }
        val locationMarker = locationComponent.model.value.currentMarker
        if (locationMarker != syncedRulerLocation) {
            rulerComponent.onLocationUpdated(locationMarker)
            syncedRulerLocation = locationMarker
        }
    }

    private fun refreshModel() {
        if (pendingViewportCommand != null) {
            pendingLocationViewportCommand = null
            pendingRulerViewportCommand = null
        } else if (pendingLocationViewportCommand != null) {
            pendingRulerViewportCommand = null
        }
        mutableModel.value = mergeModels()
    }

    private fun mergeModels(): MapScreenComponent.Model {
        val mapModel = holder.model.value
        val toolsModel = toolsComponent.model.value
        val locationModel = locationComponent.model.value
        val rulerModel = rulerComponent.model.value
        val viewportModel = viewportComponent.model.value
        val pendingViewportCommand =
            pendingViewportCommand ?: pendingLocationViewportCommand ?: pendingRulerViewportCommand

        return mapModel.copy(
            mapState = mapModel.mapState.copy(
                style = toolsModel.selectedStyle,
                overlayLayers = toolsModel.layers,
            ),
            availableMapCatalog = toolsModel.availableMapCatalog,
            lastCameraSnapshot = viewportModel.cameraSnapshot ?: mapModel.lastCameraSnapshot,
            isMapToolsMenuVisible = toolsModel.isMenuVisible,
            isAvailableMapsSheetVisible = toolsModel.isAvailableMapsSheetVisible,
            selectedAvailableMap = toolsModel.selectedAvailableMap,
            isMapsOnScreenSheetVisible = toolsModel.isMapsOnScreenSheetVisible,
            selectedOverlayLayer = toolsModel.selectedOverlayLayer,
            editingOverlayOpacityLayer = toolsModel.editingOverlayOpacityLayer,
            myLocationMode = locationModel.mode,
            currentLocationMarker = locationModel.currentMarker,
            pendingLocationRequest = locationModel.pendingRequest,
            isRulerEnabled = rulerModel.isEnabled,
            rulerMeasurement = rulerModel.measurement,
            rulerInfoWindow = rulerModel.infoWindow,
            pendingViewportCommand = pendingViewportCommand,
            isCenterMarkerMenuVisible = viewportModel.isCenterMarkerMenuVisible,
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

    private fun handleViewportCommandRequested(command: MapViewportCommand) {
        pendingViewportCommand = command
        refreshModel()
    }

    private fun handleToolsLayersChanged(layers: List<MapLayerEntry>) {
        if (layers != toolsComponent.model.value.layers) {
            return
        }
        refreshModel()
    }

    private fun dismissToolsMenuIfVisible() {
        if (toolsComponent.model.value.isMenuVisible) {
            toolsComponent.onMapToolsDismiss()
        }
    }

    private fun dismissViewportMenuIfVisible() {
        if (viewportComponent.model.value.isCenterMarkerMenuVisible) {
            viewportComponent.onCenterMarkerMenuDismiss()
        }
    }

    private fun shouldDismissViewportMenu(intent: MapStore.Intent): Boolean = when (intent) {
        is MapStore.Intent.CameraIdle,
        is MapStore.Intent.CreatePoint,
        is MapStore.Intent.Drawing,
        is MapStore.Intent.FeatureSelection,
        -> true
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
