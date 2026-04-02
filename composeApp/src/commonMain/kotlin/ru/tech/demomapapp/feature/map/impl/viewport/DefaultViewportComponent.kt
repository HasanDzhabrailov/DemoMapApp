package ru.tech.demomapapp.feature.map.impl.viewport

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot

internal class DefaultViewportComponent(
    componentContext: ComponentContext,
    private val viewportStoreFactory: ViewportStoreFactory,
    private val output: ViewportComponent.Output,
) : ViewportComponent, ComponentContext by componentContext {

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        ViewportStoreHolder(viewportStoreFactory)
    }

    override val model: Value<ViewportModel> = holder.model

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = holder.accept(ViewportStore.Intent.CameraIdle(snapshot))

    override fun onZoomInClick() = holder.accept(ViewportStore.Intent.ZoomInClicked)

    override fun onZoomOutClick() = holder.accept(ViewportStore.Intent.ZoomOutClicked)

    override fun onViewportCommandConsumed() = holder.accept(ViewportStore.Intent.ViewportCommandConsumed)

    override fun onCenterMarkerClick() = holder.accept(ViewportStore.Intent.CenterMarkerClicked)

    override fun onCenterMarkerMenuDismiss() = holder.accept(ViewportStore.Intent.CenterMarkerMenuDismissed)

    init {
        holder.labels { label ->
            when (label) {
                is ViewportStore.Label.ViewportCommandRequested -> output.onViewportCommandRequested(label.command)
            }
        }
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultViewportComponent.viewportStoreHolder"
    }
}
