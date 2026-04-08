package ru.tech.demomapapp.feature.map.viewport

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot

internal class DefaultViewportComponent(
    componentContext: ComponentContext,
    private val viewportStoreFactory: ViewportStoreFactory,
    initialModel: ViewportModel = ViewportModel(),
    private val output: ViewportComponent.Output,
) : ViewportComponent, ComponentContext by componentContext {
    private val navigation = SlotNavigation<Config>()

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        ViewportStoreHolder(viewportStoreFactory, initialModel)
    }
    private val mutableModel = MutableValue(initialModel)
    override val childSlot: Value<ChildSlot<*, ViewportComponent.Child>> = childSlot(
        source = navigation,
        serializer = Config.serializer(),
        handleBackButton = false,
        childFactory = ::createChild,
    )
    private val states = holder.states {
        syncModel()
        output.onStateChanged()
    }

    override val model: Value<ViewportModel> = mutableModel

    override fun onCameraIdle(snapshot: MapCameraSnapshot) = holder.accept(ViewportStore.Intent.CameraIdle(snapshot))

    override fun onZoomInClick() = holder.accept(ViewportStore.Intent.ZoomInClicked)

    override fun onZoomOutClick() = holder.accept(ViewportStore.Intent.ZoomOutClicked)

    override fun onViewportCommandConsumed() = holder.accept(ViewportStore.Intent.ViewportCommandConsumed)

    override fun onCenterMarkerClick() {
        navigation.activate(Config.Menu)
        syncModel()
        output.onStateChanged()
    }

    override fun onCenterMarkerMenuDismiss() {
        navigation.dismiss()
        syncModel()
        output.onStateChanged()
    }

    init {
        if (initialModel.isCenterMarkerMenuVisible) {
            navigation.activate(Config.Menu)
        }
        syncModel()

        holder.labels { label ->
            when (label) {
                is ViewportStore.Label.ViewportCommandRequested -> output.onViewportCommandRequested(label.command)
            }
        }
    }

    @Suppress("UnusedParameter")
    private fun createChild(config: Config, componentContext: ComponentContext): ViewportComponent.Child =
        when (config) {
            Config.Menu -> ViewportComponent.Child.Menu
        }

    private fun syncModel() {
        mutableModel.value = holder.model.value.copy(
            isCenterMarkerMenuVisible = childSlot.value.child?.instance is ViewportComponent.Child.Menu,
        )
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Menu : Config
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultViewportComponent.viewportStoreHolder"
    }
}
