package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tech.demomapapp.feature.map.impl.ShapeDrawingDraftUpdater

internal class MapStoreReducer(
    private val shapeDrawingDraftUpdater: ShapeDrawingDraftUpdater,
) : Reducer<MapStore.State, MapStoreMessage> {
    override fun MapStore.State.reduce(msg: MapStoreMessage): MapStore.State {
        reduceLayerManagementMessage(msg)?.let { return it }
        return when (msg) {
            is MapStoreMessage.CameraIdleReceived -> copy(
                lastCameraSnapshot = msg.snapshot,
                selectedFeatureInfoWindow = null,
            )

            is MapStoreMessage.CenterMarkerMenuDismissed -> copy(
                isCenterMarkerMenuVisible = false,
            )

            is MapStoreMessage.CenterMarkerMenuOpened -> {
                if (drawingMode != null) {
                    this
                } else {
                    copy(
                        isMapToolsMenuVisible = false,
                        isCenterMarkerMenuVisible = true,
                        selectedFeatureInfoWindow = null,
                    )
                }
            }

            is MapStoreMessage.CreatePointLatitudeChanged -> updateCreatePointDraft {
                copy(latitudeInput = msg.value)
            }

            is MapStoreMessage.CreatePointLongitudeChanged -> updateCreatePointDraft {
                copy(longitudeInput = msg.value)
            }

            is MapStoreMessage.CreatePointSheetDismissed -> copy(
                isCreatePointSheetVisible = false,
                createPointDraft = null,
            )

            is MapStoreMessage.CreatePointSheetOpened -> copy(
                isMapToolsMenuVisible = false,
                isCenterMarkerMenuVisible = false,
                isCreatePointSheetVisible = lastCameraSnapshot != null,
                createPointDraft = lastCameraSnapshot?.toCreatePointDraft(),
                selectedFeatureInfoWindow = null,
            )

            is MapStoreMessage.CreatePointTitleChanged -> updateCreatePointDraft {
                copy(titleInput = msg.value)
            }

            is MapStoreMessage.CreatePointCreated -> copy(
                mapState = mapState.copy(points = mapState.points + msg.point),
                isCreatePointSheetVisible = false,
                createPointDraft = null,
            )

            is MapStoreMessage.DrawingDismissed -> copy(
                drawingMode = null,
                shapeDrawingDraft = null,
                isCreateShapeSheetVisible = false,
            )

            is MapStoreMessage.DrawingStarted -> copy(
                isMapToolsMenuVisible = false,
                isCenterMarkerMenuVisible = false,
                isCreatePointSheetVisible = false,
                createPointDraft = null,
                drawingMode = msg.mode,
                shapeDrawingDraft = MapStore.ShapeDrawingDraft(mode = msg.mode),
                isCreateShapeSheetVisible = false,
                selectedFeatureInfoWindow = null,
            )

            is MapStoreMessage.DrawingPositionAdded -> updateShapeDrawingDraft(
                clearSelectedFeatureInfoWindow = true,
            ) { draft ->
                shapeDrawingDraftUpdater.addVertex(draft, msg.snapshot)
            }

            is MapStoreMessage.DrawingLastPositionRemoved -> updateShapeDrawingDraft { draft ->
                shapeDrawingDraftUpdater.removeLastVertex(draft)
            }

            is MapStoreMessage.FeatureInfoWindowOpened -> copy(
                isMapToolsMenuVisible = false,
                isCenterMarkerMenuVisible = false,
                selectedFeatureInfoWindow = msg.infoWindow,
            )

            is MapStoreMessage.FeatureInfoWindowDismissed -> copy(
                selectedFeatureInfoWindow = null,
            )

            is MapStoreMessage.LineCreated -> copy(
                mapState = mapState.copy(lines = mapState.lines + msg.line),
                drawingMode = null,
                shapeDrawingDraft = null,
                isCreateShapeSheetVisible = false,
            )
            is MapStoreMessage.MapToolsMenuDismissed -> copy(
                isMapToolsMenuVisible = false,
            )

            is MapStoreMessage.MapToolsMenuToggled -> {
                val isMenuVisible = !isMapToolsMenuVisible
                copy(
                    isMapToolsMenuVisible = isMenuVisible,
                    isCenterMarkerMenuVisible = if (isMenuVisible) {
                        false
                    } else {
                        isCenterMarkerMenuVisible
                    },
                    isAvailableMapsSheetVisible = false,
                    isMapsOnScreenSheetVisible = false,
                    selectedAvailableMap = null,
                    selectedOverlayLayer = null,
                    editingOverlayOpacityLayer = null,
                    selectedFeatureInfoWindow = if (isMenuVisible) {
                        null
                    } else {
                        selectedFeatureInfoWindow
                    },
                )
            }

            is MapStoreMessage.ShapeSheetDismissed -> copy(
                isCreateShapeSheetVisible = false,
            )

            is MapStoreMessage.ShapeSheetOpened -> {
                val draft = shapeDrawingDraft
                if (draft != null && draft.canOpenDetails()) {
                    copy(isCreateShapeSheetVisible = true)
                } else {
                    this
                }
            }

            is MapStoreMessage.ShapeTitleChanged -> {
                val draft = shapeDrawingDraft ?: return this
                copy(shapeDrawingDraft = draft.copy(titleInput = msg.value))
            }

            is MapStoreMessage.PolygonCreated -> copy(
                mapState = mapState.copy(polygons = mapState.polygons + msg.polygon),
                drawingMode = null,
                shapeDrawingDraft = null,
                isCreateShapeSheetVisible = false,
            )

            is MapStoreMessage.StateSynced -> msg.state
            else -> this
        }
    }

    private fun MapStore.State.updateCreatePointDraft(
        transform: MapStore.CreatePointDraft.() -> MapStore.CreatePointDraft,
    ): MapStore.State {
        val draft = createPointDraft ?: return this
        return copy(createPointDraft = draft.transform())
    }

    private fun MapStore.State.updateShapeDrawingDraft(
        clearSelectedFeatureInfoWindow: Boolean = false,
        transform: (MapStore.ShapeDrawingDraft) -> MapStore.ShapeDrawingDraft,
    ): MapStore.State {
        val draft = shapeDrawingDraft ?: return this
        return copy(
            shapeDrawingDraft = transform(draft),
            selectedFeatureInfoWindow = if (clearSelectedFeatureInfoWindow) {
                null
            } else {
                selectedFeatureInfoWindow
            },
        )
    }

    private fun MapStore.ShapeDrawingDraft.canOpenDetails(): Boolean = fixedVertices.size >= minimumVertexCount()

    private fun MapStore.ShapeDrawingDraft.minimumVertexCount(): Int = when (mode) {
        MapStore.DrawingMode.LINE -> 2
        MapStore.DrawingMode.POLYGON -> 3
    }
}
