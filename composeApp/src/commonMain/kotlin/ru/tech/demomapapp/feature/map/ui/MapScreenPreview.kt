package ru.tech.demomapapp.feature.map.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapScreenUiContract
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.drawing.DefaultDrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingStoreFactory
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.location.LocationModel
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.ruler.RulerModel
import ru.tech.demomapapp.feature.map.tools.DefaultToolsComponent
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.viewport.ViewportComponent
import ru.tech.demomapapp.feature.map.viewport.ViewportModel

@Preview
@Composable
internal fun MapScreenContentPreview() {
    MaterialTheme {
        MapScreenContent(component = PreviewMapScreenComponent())
    }
}

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

    override val drawingComponent: DrawingComponent = DefaultDrawingComponent(
        componentContext = DefaultComponentContext(LifecycleRegistry()),
        drawingStoreFactory = DrawingStoreFactory(),
        output = object : DrawingComponent.Output {
            override fun onStateChanged() = Unit
            override fun onFeatureCreated(feature: DrawingComponent.CreatedFeature) = Unit
        },
    )

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

    override val toolsComponent: ToolsComponent = DefaultToolsComponent(
        componentContext = DefaultComponentContext(LifecycleRegistry()),
        toolsStoreFactory = ru.tech.demomapapp.feature.map.tools.ToolsStoreFactory(),
        initialModel = model.value,
        output = object : ToolsComponent.Output {
            override fun onStateChanged() = Unit
            override fun onLayersChanged(layers: List<ru.tech.demomapapp.feature.map.api.MapLayerEntry>) = Unit
        },
    )

    override val viewportComponent: ViewportComponent = object : ViewportComponent {
        override val model: Value<ViewportModel> = MutableValue(ViewportModel())
        override val childSlot: Value<ChildSlot<*, ViewportComponent.Child>> = MutableValue(
            ChildSlot<Nothing, ViewportComponent.Child>()
        )

        override fun onCameraIdle(snapshot: MapCameraSnapshot) = Unit
        override fun onZoomInClick() = Unit
        override fun onZoomOutClick() = Unit
        override fun onViewportCommandConsumed() = Unit
        override fun onCenterMarkerClick() = Unit
        override fun onCenterMarkerMenuDismiss() = Unit
    }

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = Unit
    override fun onMapToolsClick() = Unit
    override fun onAvailableMapsClick() = Unit
    override fun onMapsOnScreenClick() = Unit
    override fun onRulerToggle() = Unit
    override fun onViewportCommandConsumed() = Unit
    override fun onCenterMarkerClick() = Unit
    override fun onCreatePointClick() = Unit
    override fun onCreateLineClick() = Unit
    override fun onCreatePolygonClick() = Unit
    override fun onDrawingAddPositionClick() = Unit
    override fun onFeatureClick(
        featureKey: String,
        featureType: MapScreenComponent.FeatureType,
        anchor: MapScreenComponent.FeatureInfoWindowAnchor,
    ) = Unit
    override fun onFeatureInfoWindowDismiss() = Unit
}
