package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreFactory
import ru.tech.demomapapp.feature.map.impl.store.MapStoreHolder
import ru.tech.demomapapp.feature.map.impl.store.toStoreAnchor
import ru.tech.demomapapp.feature.map.impl.store.toStoreFeatureType

internal class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    private val mapStoreFactory: MapStoreFactory = MapStoreFactory(),
) : MapScreenComponent, ComponentContext by componentContext {
    private val holder = instanceKeeper.getOrCreate(key = MAP_STORE_HOLDER_KEY) {
        MapStoreHolder(
            mapStoreFactory = mapStoreFactory,
            initialModel = MapScreenComponent.Model(),
        )
    }
    override val model: Value<MapScreenComponent.Model> = holder.model

    override fun onCameraIdle(snapshot: MapCameraSnapshot) =
        holder.accept(MapStore.Intent.Viewport.CameraIdle(snapshot))

    override fun onMapToolsClick() = holder.accept(MapStore.Intent.Tools.MapToolsClicked)
    override fun onMapToolsDismiss() = holder.accept(MapStore.Intent.Tools.MapToolsDismissed)
    override fun onZoomInClick() = holder.accept(MapStore.Intent.Viewport.ZoomInClicked)
    override fun onZoomOutClick() = holder.accept(MapStore.Intent.Viewport.ZoomOutClicked)
    override fun onAvailableMapsClick() = holder.accept(MapStore.Intent.Tools.AvailableMapsClicked)
    override fun onMapsOnScreenClick() = holder.accept(MapStore.Intent.Tools.MapsOnScreenClicked)
    override fun onGpsToggle() = holder.accept(MapStore.Intent.Location.GpsToggled)
    override fun onMyLocationClick() = holder.accept(MapStore.Intent.Location.MyLocationClicked)
    override fun onCurrentLocationFocusClick() = holder.accept(MapStore.Intent.Location.CurrentLocationFocusClicked)
    override fun onLocationRequestConsumed() = holder.accept(MapStore.Intent.Location.LocationRequestConsumed)
    override fun onLocationResult(result: LocationRequestResult) = holder.accept(MapStore.Intent.Location.LocationResultReceived(result))
    override fun onRulerToggle() = holder.accept(MapStore.Intent.Ruler.Toggled)
    override fun onViewportCommandConsumed() = holder.accept(MapStore.Intent.Viewport.ViewportCommandConsumed)
    override fun onCenterMarkerClick() = holder.accept(MapStore.Intent.CenterMarker.Clicked)
    override fun onCenterMarkerMenuDismiss() = holder.accept(MapStore.Intent.CenterMarker.MenuDismissed)
    override fun onCreatePointClick() = holder.accept(MapStore.Intent.CreatePoint.Clicked)
    override fun onCreateLineClick() = holder.accept(MapStore.Intent.Drawing.CreateLineClicked)
    override fun onCreatePolygonClick() = holder.accept(MapStore.Intent.Drawing.CreatePolygonClicked)
    override fun onCreatePointLatitudeChange(value: String) = holder.accept(MapStore.Intent.CreatePoint.LatitudeChanged(value))
    override fun onCreatePointLongitudeChange(value: String) = holder.accept(MapStore.Intent.CreatePoint.LongitudeChanged(value))
    override fun onCreatePointTitleChange(value: String) = holder.accept(MapStore.Intent.CreatePoint.TitleChanged(value))
    override fun onCreatePointConfirm() = holder.accept(MapStore.Intent.CreatePoint.Confirmed)
    override fun onCreatePointSheetDismiss() = holder.accept(MapStore.Intent.CreatePoint.SheetDismissed)
    override fun onDrawingAddPositionClick() = holder.accept(MapStore.Intent.Drawing.AddPositionClicked)
    override fun onDrawingRemoveLastPositionClick() = holder.accept(MapStore.Intent.Drawing.RemoveLastPositionClicked)
    override fun onDrawingDetailsClick() = holder.accept(MapStore.Intent.Drawing.DetailsClicked)
    override fun onDrawingDismiss() = holder.accept(MapStore.Intent.Drawing.Dismissed)
    override fun onCreateShapeTitleChange(value: String) = holder.accept(MapStore.Intent.Drawing.TitleChanged(value))
    override fun onCreateShapeConfirm() = holder.accept(MapStore.Intent.Drawing.Confirmed)
    override fun onCreateShapeSheetDismiss() = holder.accept(MapStore.Intent.Drawing.ShapeSheetDismissed)

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) = holder.accept(
        MapStore.Intent.FeatureSelection.FeatureClicked(
            featureKey = featureKey,
            featureType = featureType.toStoreFeatureType(),
            anchor = anchor.toStoreAnchor(),
        )
    )

    override fun onFeatureInfoWindowDismiss() = holder.accept(MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed)

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