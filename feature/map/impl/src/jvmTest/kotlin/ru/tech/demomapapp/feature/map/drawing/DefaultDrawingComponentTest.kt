package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapVertex

class DefaultDrawingComponentTest {

    @Test
    fun `create point click opens point sheet slot`() {
        val component = createComponent()

        component.onCreatePointClick()

        assertTrue(component.pointSheetSlot.value.child?.instance is DrawingComponent.PointSheetChild.Content)
    }

    @Test
    fun `drawing details opens shape sheet slot when store allows it`() {
        val component = createComponent(
            initialModel = DrawingModel(
                drawingMode = DrawingMode.LINE,
                shapeDrawingDraft = ShapeDrawingDraft(
                    mode = DrawingMode.LINE,
                    fixedVertices = listOf(
                        MapVertex(55.75, 37.61),
                        MapVertex(55.76, 37.62),
                    ),
                ),
            ),
        )

        component.onDrawingDetailsClick()

        assertTrue(component.shapeSheetSlot.value.child?.instance is DrawingComponent.ShapeSheetChild.Content)
    }

    @Test
    fun `initial visible sheets restore navigation slots`() {
        val component = createComponent(
            initialModel = DrawingModel(
                isCreatePointSheetVisible = true,
                isCreateShapeSheetVisible = true,
            ),
        )

        assertTrue(component.pointSheetSlot.value.child?.instance is DrawingComponent.PointSheetChild.Content)
        assertTrue(component.shapeSheetSlot.value.child?.instance is DrawingComponent.ShapeSheetChild.Content)
    }

    private fun createComponent(initialModel: DrawingModel = DrawingModel()): DefaultDrawingComponent {
        return DefaultDrawingComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            drawingStoreFactory = DrawingStoreFactory(),
            initialModel = initialModel,
            output = object : DrawingComponent.Output {
                override fun onStateChanged() = Unit

                override fun onFeatureCreated(feature: DrawingComponent.CreatedFeature) = Unit
            },
        )
    }
}
