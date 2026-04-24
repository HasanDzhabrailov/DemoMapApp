package ru.tech.demomapapp.feature.map.drawing.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.DrawingUiContract

@Composable
internal fun BoxScope.DrawingContent(component: DrawingUiContract, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()
    val pointSheetSlot by component.pointSheetSlot.subscribeAsState()
    val shapeSheetSlot by component.shapeSheetSlot.subscribeAsState()

    model.shapeDrawingDraft?.let { draft ->
        ShapeDrawingControlsOverlay(
            mode = draft.mode,
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
                draft = draft,
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
                draft = draft,
                onTitleChange = component::onCreateShapeTitleChange,
                onConfirm = component::onCreateShapeConfirm,
                onDismiss = component::onCreateShapeSheetDismiss,
            )
        }
    }
}
