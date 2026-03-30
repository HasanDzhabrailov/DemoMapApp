package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Reducer

internal object MapStoreReducer : Reducer<MapStore.State, MapStoreMessage> {
    override fun MapStore.State.reduce(msg: MapStoreMessage): MapStore.State =
        when (msg) {
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

            is MapStoreMessage.FeatureInfoWindowDismissed -> copy(
                selectedFeatureInfoWindow = null,
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

            is MapStoreMessage.StateSynced -> msg.state
        }

    private fun MapStore.State.updateCreatePointDraft(
        transform: MapStore.CreatePointDraft.() -> MapStore.CreatePointDraft,
    ): MapStore.State {
        val draft = createPointDraft ?: return this
        return copy(createPointDraft = draft.transform())
    }

    private fun MapStore.ShapeDrawingDraft.canOpenDetails(): Boolean =
        fixedVertices.size >= minimumVertexCount()

    private fun MapStore.ShapeDrawingDraft.minimumVertexCount(): Int =
        when (mode) {
            MapStore.DrawingMode.LINE -> 2
            MapStore.DrawingMode.POLYGON -> 3
        }
}
