package ru.tech.demomapapp.feature.map.drawing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot

internal class DefaultDrawingComponent(
    componentContext: ComponentContext,
    private val drawingStoreFactory: DrawingStoreFactory,
    initialModel: DrawingModel = DrawingModel(),
    private val output: DrawingComponent.Output,
) : DrawingComponent, ComponentContext by componentContext {

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        DrawingStoreHolder(drawingStoreFactory, initialModel)
    }
    private val states = holder.states { output.onStateChanged() }

    override val model: Value<DrawingModel> = holder.model

    override fun onCreatePointClick() = holder.accept(DrawingStore.Intent.CreatePointClicked)
    override fun onCreateLineClick() = holder.accept(DrawingStore.Intent.CreateLineClicked)
    override fun onCreatePolygonClick() = holder.accept(DrawingStore.Intent.CreatePolygonClicked)
    override fun onCreatePointLatitudeChange(value: String) =
        holder.accept(DrawingStore.Intent.PointLatitudeChanged(value))
    override fun onCreatePointLongitudeChange(value: String) =
        holder.accept(DrawingStore.Intent.PointLongitudeChanged(value))
    override fun onCreatePointTitleChange(value: String) = holder.accept(DrawingStore.Intent.PointTitleChanged(value))
    override fun onCreatePointConfirm() = holder.accept(DrawingStore.Intent.PointConfirmed)
    override fun onCreatePointSheetDismiss() = holder.accept(DrawingStore.Intent.PointSheetDismissed)
    override fun onDrawingAddPositionClick() = holder.accept(DrawingStore.Intent.DrawingAddPositionClicked)
    override fun onDrawingRemoveLastPositionClick() = holder.accept(
        DrawingStore.Intent.DrawingRemoveLastPositionClicked,
    )
    override fun onDrawingDetailsClick() = holder.accept(DrawingStore.Intent.DrawingDetailsClicked)
    override fun onDrawingDismiss() = holder.accept(DrawingStore.Intent.DrawingDismissed)
    override fun onCreateShapeTitleChange(value: String) = holder.accept(DrawingStore.Intent.ShapeTitleChanged(value))
    override fun onCreateShapeConfirm() = holder.accept(DrawingStore.Intent.ShapeConfirmed)
    override fun onCreateShapeSheetDismiss() = holder.accept(DrawingStore.Intent.ShapeSheetDismissed)
    override fun onCameraPositionUpdated(snapshot: MapCameraSnapshot) =
        holder.accept(DrawingStore.Intent.CameraPositionUpdated(snapshot))

    init {
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

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultDrawingComponent.drawingStoreHolder"
    }
}
