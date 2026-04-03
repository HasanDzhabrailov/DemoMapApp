package ru.tech.demomapapp.feature.map.drawing.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.drawing.CreatePointDraft
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingMode
import ru.tech.demomapapp.feature.map.drawing.ShapeDrawingDraft

@Composable
internal fun BoxScope.DrawingContent(component: DrawingComponent, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()
    val pointSheetSlot by component.pointSheetSlot.subscribeAsState()
    val shapeSheetSlot by component.shapeSheetSlot.subscribeAsState()

    model.shapeDrawingDraft?.let { draft ->
        ShapeDrawingControlsOverlay(
            mode = draft.mode.toUiDrawingMode(),
            fixedVertexCount = draft.fixedVertices.size,
            onRemoveLastClick = component::onDrawingRemoveLastPositionClick,
            onAddPositionClick = component::onDrawingAddPositionClick,
            onDetailsClick = component::onDrawingDetailsClick,
            onDismissClick = component::onDrawingDismiss,
            modifier = modifier.align(Alignment.BottomCenter),
        )
    }

    if (pointSheetSlot.child != null) {
        model.createPointDraft?.let { draft ->
            CreatePointBottomSheet(
                draft = draft.toUiDraft(),
                onLatitudeChange = component::onCreatePointLatitudeChange,
                onLongitudeChange = component::onCreatePointLongitudeChange,
                onTitleChange = component::onCreatePointTitleChange,
                onConfirm = component::onCreatePointConfirm,
                onDismiss = component::onCreatePointSheetDismiss,
            )
        }
    }

    if (shapeSheetSlot.child != null) {
        model.shapeDrawingDraft?.let { draft ->
            CreateShapeBottomSheet(
                draft = draft.toUiDraft(),
                onTitleChange = component::onCreateShapeTitleChange,
                onConfirm = component::onCreateShapeConfirm,
                onDismiss = component::onCreateShapeSheetDismiss,
            )
        }
    }
}

private fun CreatePointDraft.toUiDraft(): MapScreenComponent.CreatePointDraft = MapScreenComponent.CreatePointDraft(
    latitudeInput = latitudeInput,
    longitudeInput = longitudeInput,
    titleInput = titleInput,
)

private fun ShapeDrawingDraft.toUiDraft(): MapScreenComponent.ShapeDrawingDraft = MapScreenComponent.ShapeDrawingDraft(
    mode = mode.toUiDrawingMode(),
    fixedVertices = fixedVertices,
    titleInput = titleInput,
)

private fun DrawingMode.toUiDrawingMode(): MapScreenComponent.DrawingMode = when (this) {
    DrawingMode.LINE -> MapScreenComponent.DrawingMode.LINE
    DrawingMode.POLYGON -> MapScreenComponent.DrawingMode.POLYGON
}
