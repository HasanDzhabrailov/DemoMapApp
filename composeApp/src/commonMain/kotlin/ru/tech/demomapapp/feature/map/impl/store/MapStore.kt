package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapCatalogItem
import ru.tech.demomapapp.feature.map.api.MapLayerCatalog
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLocationMarker
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.api.RulerInfoWindowState
import ru.tech.demomapapp.feature.map.api.RulerMeasurement

internal interface MapStore : Store<MapStore.Intent, MapStore.State, MapStore.Label> {
    sealed interface Intent {
        sealed interface Viewport : Intent {
            data class CameraIdle(val snapshot: MapCameraSnapshot) : Viewport

            object ZoomInClicked : Viewport

            object ZoomOutClicked : Viewport

            object ViewportCommandConsumed : Viewport
        }

        sealed interface Tools : Intent {
            object MapToolsClicked : Tools

            object MapToolsDismissed : Tools

            object AvailableMapsClicked : Tools

            object AvailableMapsDismissed : Tools

            data class AvailableMapSelected(val mapId: String) : Tools

            object AvailableMapConfirmed : Tools

            object AvailableMapSelectionDismissed : Tools

            object MapsOnScreenClicked : Tools

            object MapsOnScreenDismissed : Tools

            data class OverlayLayerActionsClicked(val layerId: String) : Tools

            object OverlayLayerActionsDismissed : Tools

            object OverlayLayerMoveUpClicked : Tools

            object OverlayLayerMoveDownClicked : Tools

            object OverlayLayerRemoveClicked : Tools

            object OverlayLayerOpacityClicked : Tools

            data class OverlayLayerOpacityChanged(val value: Float) : Tools

            object OverlayLayerOpacityDismissed : Tools
        }

        sealed interface Location : Intent {
            object GpsToggled : Location

            object MyLocationClicked : Location

            object CurrentLocationFocusClicked : Location

            object LocationRequestConsumed : Location

            data class LocationResultReceived(val result: LocationRequestResult) : Location
        }

        sealed interface Ruler : Intent {
            object Toggled : Ruler
        }

        sealed interface CenterMarker : Intent {
            object Clicked : CenterMarker

            object MenuDismissed : CenterMarker
        }

        sealed interface CreatePoint : Intent {
            object Clicked : CreatePoint

            data class LatitudeChanged(val value: String) : CreatePoint

            data class LongitudeChanged(val value: String) : CreatePoint

            data class TitleChanged(val value: String) : CreatePoint

            object Confirmed : CreatePoint

            object SheetDismissed : CreatePoint
        }

        sealed interface Drawing : Intent {
            object CreateLineClicked : Drawing

            object CreatePolygonClicked : Drawing

            object AddPositionClicked : Drawing

            object RemoveLastPositionClicked : Drawing

            object DetailsClicked : Drawing

            object Dismissed : Drawing

            data class TitleChanged(val value: String) : Drawing

            object Confirmed : Drawing

            object ShapeSheetDismissed : Drawing
        }

        sealed interface FeatureSelection : Intent {
            data class FeatureClicked(
                val featureKey: String,
                val featureType: FeatureType,
                val anchor: FeatureInfoWindowAnchor,
            ) : FeatureSelection

            object FeatureInfoWindowDismissed : FeatureSelection
        }
    }

    data class State(
        val mapState: MapState = MapState(),
        val availableMapCatalog: List<MapCatalogItem> = MapLayerCatalog.items(),
        val lastCameraSnapshot: MapCameraSnapshot? = null,
        val isMapToolsMenuVisible: Boolean = false,
        val isAvailableMapsSheetVisible: Boolean = false,
        val selectedAvailableMap: MapCatalogItem? = null,
        val isMapsOnScreenSheetVisible: Boolean = false,
        val selectedOverlayLayer: MapLayerEntry? = null,
        val editingOverlayOpacityLayer: MapLayerEntry? = null,
        val myLocationMode: MyLocationMode = MyLocationMode.OFF,
        val currentLocationMarker: MapLocationMarker? = null,
        val activeLocationRequest: MapLocationRequest? = null,
        val isRulerEnabled: Boolean = false,
        val rulerMeasurement: RulerMeasurement? = null,
        val rulerInfoWindow: RulerInfoWindowState? = null,
        val isCenterMarkerMenuVisible: Boolean = false,
        val isCreatePointSheetVisible: Boolean = false,
        val createPointDraft: CreatePointDraft? = null,
        val drawingMode: DrawingMode? = null,
        val shapeDrawingDraft: ShapeDrawingDraft? = null,
        val isCreateShapeSheetVisible: Boolean = false,
        val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    ) {
        fun toModel(): MapScreenComponent.Model = MapScreenComponent.Model(
            mapState = mapState,
            availableMapCatalog = availableMapCatalog,
            lastCameraSnapshot = lastCameraSnapshot,
            isMapToolsMenuVisible = isMapToolsMenuVisible,
            isAvailableMapsSheetVisible = isAvailableMapsSheetVisible,
            selectedAvailableMap = selectedAvailableMap,
            isMapsOnScreenSheetVisible = isMapsOnScreenSheetVisible,
            selectedOverlayLayer = selectedOverlayLayer,
            editingOverlayOpacityLayer = editingOverlayOpacityLayer,
            myLocationMode = myLocationMode,
            currentLocationMarker = currentLocationMarker,
            isRulerEnabled = isRulerEnabled,
            rulerMeasurement = rulerMeasurement,
            rulerInfoWindow = rulerInfoWindow,
            isCenterMarkerMenuVisible = isCenterMarkerMenuVisible,
            isCreatePointSheetVisible = isCreatePointSheetVisible,
            createPointDraft = createPointDraft?.toComponentDraft(),
            drawingMode = drawingMode?.toComponentDrawingMode(),
            shapeDrawingDraft = shapeDrawingDraft?.toComponentDraft(),
            isCreateShapeSheetVisible = isCreateShapeSheetVisible,
            selectedFeatureInfoWindow = selectedFeatureInfoWindow?.toComponentInfoWindow(),
        )

        companion object {
            fun fromModel(model: MapScreenComponent.Model, activeLocationRequest: MapLocationRequest? = null): State =
                State(
                    mapState = model.mapState,
                    availableMapCatalog = model.availableMapCatalog,
                    lastCameraSnapshot = model.lastCameraSnapshot,
                    isMapToolsMenuVisible = model.isMapToolsMenuVisible,
                    isAvailableMapsSheetVisible = model.isAvailableMapsSheetVisible,
                    selectedAvailableMap = model.selectedAvailableMap,
                    isMapsOnScreenSheetVisible = model.isMapsOnScreenSheetVisible,
                    selectedOverlayLayer = model.selectedOverlayLayer,
                    editingOverlayOpacityLayer = model.editingOverlayOpacityLayer,
                    myLocationMode = model.myLocationMode,
                    currentLocationMarker = model.currentLocationMarker,
                    activeLocationRequest = activeLocationRequest,
                    isRulerEnabled = model.isRulerEnabled,
                    rulerMeasurement = model.rulerMeasurement,
                    rulerInfoWindow = model.rulerInfoWindow,
                    isCenterMarkerMenuVisible = model.isCenterMarkerMenuVisible,
                    isCreatePointSheetVisible = model.isCreatePointSheetVisible,
                    createPointDraft = model.createPointDraft?.toStoreDraft(),
                    drawingMode = model.drawingMode?.toStoreDrawingMode(),
                    shapeDrawingDraft = model.shapeDrawingDraft?.toStoreDraft(),
                    isCreateShapeSheetVisible = model.isCreateShapeSheetVisible,
                    selectedFeatureInfoWindow = model.selectedFeatureInfoWindow?.toStoreInfoWindow(),
                )
        }
    }

    enum class DrawingMode {
        LINE,
        POLYGON,
    }

    enum class FeatureType {
        POINT,
        LINE,
        POLYGON,
    }

    data class CreatePointDraft(
        val latitudeInput: String,
        val longitudeInput: String,
        val titleInput: String = "",
    )

    data class ShapeDrawingDraft(
        val mode: DrawingMode,
        val fixedVertices: List<MapVertex> = emptyList(),
        val titleInput: String = "",
    )

    data class FeatureInfoWindow(
        val title: String,
        val createdAtText: String,
        val anchor: FeatureInfoWindowAnchor,
    )

    data class FeatureInfoWindowAnchor(
        val screenX: Int,
        val screenY: Int,
    )

    sealed interface Label {
        sealed interface Viewport : Label {
            data class CommandRequested(val command: MapViewportCommand) : Viewport
        }

        sealed interface Location : Label {
            data class RequestIssued(val request: MapLocationRequest) : Location
        }

        data class NotificationRequested(val message: String) : Label
    }
}

private fun MapStore.CreatePointDraft.toComponentDraft(): MapScreenComponent.CreatePointDraft =
    MapScreenComponent.CreatePointDraft(
        latitudeInput = latitudeInput,
        longitudeInput = longitudeInput,
        titleInput = titleInput,
    )

private fun MapScreenComponent.CreatePointDraft.toStoreDraft(): MapStore.CreatePointDraft = MapStore.CreatePointDraft(
    latitudeInput = latitudeInput,
    longitudeInput = longitudeInput,
    titleInput = titleInput,
)

private fun MapStore.DrawingMode.toComponentDrawingMode(): MapScreenComponent.DrawingMode = when (this) {
    MapStore.DrawingMode.LINE -> MapScreenComponent.DrawingMode.LINE
    MapStore.DrawingMode.POLYGON -> MapScreenComponent.DrawingMode.POLYGON
}

internal fun MapScreenComponent.DrawingMode.toStoreDrawingMode(): MapStore.DrawingMode = when (this) {
    MapScreenComponent.DrawingMode.LINE -> MapStore.DrawingMode.LINE
    MapScreenComponent.DrawingMode.POLYGON -> MapStore.DrawingMode.POLYGON
}

internal fun MapScreenComponent.FeatureType.toStoreFeatureType(): MapStore.FeatureType = when (this) {
    MapScreenComponent.FeatureType.POINT -> MapStore.FeatureType.POINT
    MapScreenComponent.FeatureType.LINE -> MapStore.FeatureType.LINE
    MapScreenComponent.FeatureType.POLYGON -> MapStore.FeatureType.POLYGON
}

private fun MapStore.ShapeDrawingDraft.toComponentDraft(): MapScreenComponent.ShapeDrawingDraft =
    MapScreenComponent.ShapeDrawingDraft(
        mode = mode.toComponentDrawingMode(),
        fixedVertices = fixedVertices,
        titleInput = titleInput,
    )

internal fun MapScreenComponent.ShapeDrawingDraft.toStoreDraft(): MapStore.ShapeDrawingDraft =
    MapStore.ShapeDrawingDraft(
        mode = mode.toStoreDrawingMode(),
        fixedVertices = fixedVertices,
        titleInput = titleInput,
    )

internal fun MapStore.FeatureInfoWindow.toComponentInfoWindow(): MapScreenComponent.FeatureInfoWindow =
    MapScreenComponent.FeatureInfoWindow(
        title = title,
        createdAtText = createdAtText,
        anchor = anchor.toComponentAnchor(),
    )

internal fun MapScreenComponent.FeatureInfoWindow.toStoreInfoWindow(): MapStore.FeatureInfoWindow =
    MapStore.FeatureInfoWindow(
        title = title,
        createdAtText = createdAtText,
        anchor = anchor.toStoreAnchor(),
    )

private fun MapStore.FeatureInfoWindowAnchor.toComponentAnchor(): MapScreenComponent.FeatureInfoWindowAnchor =
    MapScreenComponent.FeatureInfoWindowAnchor(
        screenX = screenX,
        screenY = screenY,
    )

internal fun MapScreenComponent.FeatureInfoWindowAnchor.toStoreAnchor(): MapStore.FeatureInfoWindowAnchor =
    MapStore.FeatureInfoWindowAnchor(
        screenX = screenX,
        screenY = screenY,
    )

internal fun MapCameraSnapshot.toCreatePointDraft(): MapStore.CreatePointDraft = MapStore.CreatePointDraft(
    latitudeInput = latitude.toString(),
    longitudeInput = longitude.toString(),
)
