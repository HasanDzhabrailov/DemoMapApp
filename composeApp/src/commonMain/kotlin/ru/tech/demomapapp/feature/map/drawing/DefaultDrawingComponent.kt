package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot

internal class DefaultDrawingComponent(
    componentContext: ComponentContext,
    private val drawingStoreFactory: DrawingStoreFactory,
    initialModel: DrawingModel = DrawingModel(),
    private val output: DrawingComponent.Output,
) : DrawingComponent, ComponentContext by componentContext {
    private val pointSheetNavigation = SlotNavigation<PointSheetConfig>()
    private val shapeSheetNavigation = SlotNavigation<ShapeSheetConfig>()

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        DrawingStoreHolder(drawingStoreFactory, initialModel)
    }
    private val states = holder.states { output.onStateChanged() }

    override val model: Value<DrawingModel> = holder.model
    override val pointSheetSlot: Value<ChildSlot<*, DrawingComponent.PointSheetChild>> = childSlot(
        key = POINT_SHEET_SLOT_KEY,
        source = pointSheetNavigation,
        serializer = PointSheetConfig.serializer(),
        handleBackButton = false,
        childFactory = ::createPointSheetChild,
    )
    override val shapeSheetSlot: Value<ChildSlot<*, DrawingComponent.ShapeSheetChild>> = childSlot(
        key = SHAPE_SHEET_SLOT_KEY,
        source = shapeSheetNavigation,
        serializer = ShapeSheetConfig.serializer(),
        handleBackButton = false,
        childFactory = ::createShapeSheetChild,
    )

    override fun onCreatePointClick() {
        holder.accept(DrawingStore.Intent.CreatePointClicked)
        activatePointSheet()
    }

    override fun onCreateLineClick() = holder.accept(DrawingStore.Intent.CreateLineClicked)
    override fun onCreatePolygonClick() = holder.accept(DrawingStore.Intent.CreatePolygonClicked)
    override fun onCreatePointLatitudeChange(value: String) =
        holder.accept(DrawingStore.Intent.PointLatitudeChanged(value))
    override fun onCreatePointLongitudeChange(value: String) =
        holder.accept(DrawingStore.Intent.PointLongitudeChanged(value))
    override fun onCreatePointTitleChange(value: String) = holder.accept(DrawingStore.Intent.PointTitleChanged(value))

    override fun onCreatePointConfirm() {
        holder.accept(DrawingStore.Intent.PointConfirmed)
        if (!model.value.isCreatePointSheetVisible) {
            dismissPointSheet()
        }
    }

    override fun onCreatePointSheetDismiss() {
        holder.accept(DrawingStore.Intent.PointSheetDismissed)
        dismissPointSheet()
    }

    override fun onDrawingAddPositionClick() = holder.accept(DrawingStore.Intent.DrawingAddPositionClicked)
    override fun onDrawingRemoveLastPositionClick() = holder.accept(
        DrawingStore.Intent.DrawingRemoveLastPositionClicked,
    )
    override fun onDrawingDetailsClick() {
        holder.accept(DrawingStore.Intent.DrawingDetailsClicked)
        if (model.value.isCreateShapeSheetVisible) {
            activateShapeSheet()
        }
    }

    override fun onDrawingDismiss() {
        holder.accept(DrawingStore.Intent.DrawingDismissed)
        dismissShapeSheet()
    }

    override fun onCreateShapeTitleChange(value: String) = holder.accept(DrawingStore.Intent.ShapeTitleChanged(value))

    override fun onCreateShapeConfirm() {
        holder.accept(DrawingStore.Intent.ShapeConfirmed)
        if (!model.value.isCreateShapeSheetVisible) {
            dismissShapeSheet()
        }
    }

    override fun onCreateShapeSheetDismiss() {
        holder.accept(DrawingStore.Intent.ShapeSheetDismissed)
        dismissShapeSheet()
    }

    override fun onCameraPositionUpdated(snapshot: MapCameraSnapshot) =
        holder.accept(DrawingStore.Intent.CameraPositionUpdated(snapshot))

    init {
        if (initialModel.isCreatePointSheetVisible) {
            activatePointSheet()
        }
        if (initialModel.isCreateShapeSheetVisible) {
            activateShapeSheet()
        }

        holder.labels { label ->
            when (label) {
                is DrawingStore.Label.FeatureCreated.Point -> {
                    output.onFeatureCreated(DrawingComponent.CreatedFeature.Point(label.point))
                }
                is DrawingStore.Label.FeatureCreated.Line -> {
                    output.onFeatureCreated(DrawingComponent.CreatedFeature.Line(label.line))
                }
                is DrawingStore.Label.FeatureCreated.Polygon -> {
                    output.onFeatureCreated(DrawingComponent.CreatedFeature.Polygon(label.polygon))
                }
            }
        }
    }

    private fun createPointSheetChild(
        config: PointSheetConfig,
        componentContext: ComponentContext,
    ): DrawingComponent.PointSheetChild = when (config) {
        PointSheetConfig.Content -> DrawingComponent.PointSheetChild.Content
    }

    private fun createShapeSheetChild(
        config: ShapeSheetConfig,
        componentContext: ComponentContext,
    ): DrawingComponent.ShapeSheetChild = when (config) {
        ShapeSheetConfig.Content -> DrawingComponent.ShapeSheetChild.Content
    }

    private fun activatePointSheet() {
        pointSheetNavigation.activate(PointSheetConfig.Content)
        output.onStateChanged()
    }

    private fun dismissPointSheet() {
        pointSheetNavigation.dismiss()
        output.onStateChanged()
    }

    private fun activateShapeSheet() {
        shapeSheetNavigation.activate(ShapeSheetConfig.Content)
        output.onStateChanged()
    }

    private fun dismissShapeSheet() {
        shapeSheetNavigation.dismiss()
        output.onStateChanged()
    }

    @Serializable
    private sealed interface PointSheetConfig {
        @Serializable
        data object Content : PointSheetConfig
    }

    @Serializable
    private sealed interface ShapeSheetConfig {
        @Serializable
        data object Content : ShapeSheetConfig
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultDrawingComponent.drawingStoreHolder"
        const val POINT_SHEET_SLOT_KEY = "DefaultDrawingComponent.pointSheetSlot"
        const val SHAPE_SHEET_SLOT_KEY = "DefaultDrawingComponent.shapeSheetSlot"
    }
}
