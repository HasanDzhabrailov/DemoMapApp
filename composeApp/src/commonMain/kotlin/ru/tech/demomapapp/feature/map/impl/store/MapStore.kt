package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapVertex

internal interface MapStore : Store<MapStore.Intent, MapStore.State, MapStore.Label> {
    sealed interface Intent {
        data class CameraIdle(val snapshot: MapCameraSnapshot) : Intent

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
        val lastCameraSnapshot: MapCameraSnapshot? = null,
        val isCreatePointSheetVisible: Boolean = false,
        val createPointDraft: CreatePointDraft? = null,
        val drawingMode: DrawingMode? = null,
        val shapeDrawingDraft: ShapeDrawingDraft? = null,
        val isCreateShapeSheetVisible: Boolean = false,
        val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    ) {
        fun toModel(): MapScreenComponent.Model = MapScreenComponent.Model(
            mapState = mapState,
            lastCameraSnapshot = lastCameraSnapshot,
            isCreatePointSheetVisible = isCreatePointSheetVisible,
            createPointDraft = createPointDraft?.toComponentDraft(),
            drawingMode = drawingMode?.toComponentDrawingMode(),
            shapeDrawingDraft = shapeDrawingDraft?.toComponentDraft(),
            isCreateShapeSheetVisible = isCreateShapeSheetVisible,
            selectedFeatureInfoWindow = selectedFeatureInfoWindow?.toComponentInfoWindow(),
        )

        companion object {
            fun fromModel(model: MapScreenComponent.Model): State = State(
                mapState = model.mapState,
                lastCameraSnapshot = model.lastCameraSnapshot,
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
