package ru.tech.demomapapp.feature.map.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapScreenUiContract
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingModel
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.location.LocationModel
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.ruler.RulerModel
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.tools.ToolsModel

@Preview
@Composable
internal fun MapScreenContentPreview() {
    MaterialTheme {
        MapScreenContent(component = PreviewMapScreenComponent())
    }
}

@Suppress("TooManyFunctions")
private class PreviewMapScreenComponent : MapScreenUiContract {
    override val model: Value<MapScreenComponent.Model> =
        MutableValue(
            MapScreenComponent.Model(
                lastCameraSnapshot = MapCameraSnapshot(
                    latitude = 55.75124,
                    longitude = 37.61842,
                    zoom = 12.34567,
                    bearing = 18.2,
                ),
                rulerInfoWindow = RulerInfoWindowState(
                    distanceText = "12,9 км",
                    trueAzimuthText = "A = 97° 33' 29\"",
                ),
            ),
        )

    override val drawingComponent: DrawingComponent = object : DrawingComponent {
        override val model: Value<DrawingModel> = MutableValue(DrawingModel())

        override fun onCreatePointClick() = Unit
        override fun onCreateLineClick() = Unit
        override fun onCreatePolygonClick() = Unit
        override fun onCreatePointLatitudeChange(value: String) = Unit
        override fun onCreatePointLongitudeChange(value: String) = Unit
        override fun onCreatePointTitleChange(value: String) = Unit
        override fun onCreatePointConfirm() = Unit
        override fun onCreatePointSheetDismiss() = Unit
        override fun onDrawingAddPositionClick() = Unit
        override fun onDrawingRemoveLastPositionClick() = Unit
        override fun onDrawingDetailsClick() = Unit
        override fun onDrawingDismiss() = Unit
        override fun onCreateShapeTitleChange(value: String) = Unit
        override fun onCreateShapeConfirm() = Unit
        override fun onCreateShapeSheetDismiss() = Unit
        override fun onCameraPositionUpdated(snapshot: MapCameraSnapshot) = Unit
    }

    override val locationComponent: LocationComponent = object : LocationComponent {
        override val model: Value<LocationModel> = MutableValue(
            LocationModel(
                mode = MyLocationMode.MANUAL_PLACEHOLDER,
                currentMarker = MapLocationMarker(latitude = 55.75, longitude = 37.61, isPlaceholder = true),
                pendingRequest = null,
            ),
        )

        override fun onGpsToggle() = Unit
        override fun onMyLocationClick() = Unit
        override fun onCurrentLocationFocusClick() = Unit
        override fun onLocationResult(result: LocationRequestResult) = Unit
        override fun onLocationRequestConsumed() = Unit
        override fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot) = Unit
    }

    override val rulerComponent: RulerComponent = object : RulerComponent {
        override val model: Value<RulerModel> = MutableValue(
            RulerModel(
                isEnabled = true,
                infoWindow = RulerInfoWindowState(
                    distanceText = "12,9 км",
                    trueAzimuthText = "A = 97° 33' 29\"",
                ),
            ),
        )

        override fun onToggleClicked() = Unit
        override fun onLocationUpdated(location: MapLocationMarker?) = Unit
        override fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot) = Unit
    }

    override val toolsComponent: ToolsComponent = object : ToolsComponent {
        override val model: Value<ToolsModel> = MutableValue(ToolsModel())

        override fun onMapToolsClick() = Unit
        override fun onMapToolsDismiss() = Unit
        override fun onAvailableMapsClick() = Unit
        override fun onAvailableMapsDismiss() = Unit
        override fun onAvailableMapSelect(mapId: String) = Unit
        override fun onAvailableMapConfirm() = Unit
        override fun onAvailableMapSelectionDismiss() = Unit
        override fun onMapsOnScreenClick() = Unit
        override fun onMapsOnScreenDismiss() = Unit
        override fun onLayerActionsClick(layerId: String) = Unit
        override fun onLayerActionsDismiss() = Unit
        override fun onMoveLayerUpClick() = Unit
        override fun onMoveLayerDownClick() = Unit
        override fun onRemoveLayerClick() = Unit
        override fun onLayerOpacityClick() = Unit
        override fun onLayerOpacityChange(value: Float) = Unit
        override fun onLayerOpacityDismiss() = Unit
    }

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = Unit

    override fun onMapToolsClick() = Unit

    override fun onMapToolsDismiss() = Unit

    override fun onZoomInClick() = Unit

    override fun onZoomOutClick() = Unit

    override fun onAvailableMapsClick() = Unit

    override fun onAvailableMapsDismiss() = Unit

    override fun onAvailableMapSelect(mapId: String) = Unit

    override fun onAvailableMapConfirm() = Unit

    override fun onAvailableMapSelectionDismiss() = Unit

    override fun onMapsOnScreenClick() = Unit

    override fun onMapsOnScreenDismiss() = Unit

    override fun onMapLayerActionsClick(layerId: String) = Unit

    override fun onMapLayerActionsDismiss() = Unit

    override fun onMoveLayerUpClick() = Unit

    override fun onMoveLayerDownClick() = Unit

    override fun onRemoveLayerClick() = Unit

    override fun onLayerOpacityClick() = Unit

    override fun onLayerOpacityChange(value: Float) = Unit

    override fun onLayerOpacityDismiss() = Unit

    override fun onGpsToggle() = Unit

    override fun onMyLocationClick() = Unit

    override fun onCurrentLocationFocusClick() = Unit

    override fun onLocationRequestConsumed() = Unit

    override fun onLocationResult(result: LocationRequestResult) = Unit

    override fun onRulerToggle() = Unit

    override fun onViewportCommandConsumed() = Unit

    override fun onCenterMarkerClick() = Unit

    override fun onCenterMarkerMenuDismiss() = Unit

    override fun onCreatePointClick() = Unit

    override fun onCreateLineClick() = Unit

    override fun onCreatePolygonClick() = Unit

    override fun onCreatePointLatitudeChange(value: String) = Unit

    override fun onCreatePointLongitudeChange(value: String) = Unit

    override fun onCreatePointTitleChange(value: String) = Unit

    override fun onCreatePointConfirm() = Unit

    override fun onCreatePointSheetDismiss() = Unit

    override fun onDrawingAddPositionClick() = Unit

    override fun onDrawingRemoveLastPositionClick() = Unit

    override fun onDrawingDetailsClick() = Unit

    override fun onDrawingDismiss() = Unit

    override fun onCreateShapeTitleChange(value: String) = Unit

    override fun onCreateShapeConfirm() = Unit

    override fun onCreateShapeSheetDismiss() = Unit

    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) = Unit

    override fun onFeatureInfoWindowDismiss() = Unit
}
