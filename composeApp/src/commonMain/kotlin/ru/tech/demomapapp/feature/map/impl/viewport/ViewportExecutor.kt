package ru.tech.demomapapp.feature.map.impl.viewport

import com.arkivanov.mvikotlin.core.store.Executor
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

internal class ViewportExecutor :
    Executor<ViewportStore.Intent, Nothing, ViewportStore.State, ViewportStore.Message, ViewportStore.Label> {

    private lateinit var callbacks: Executor.Callbacks<
        ViewportStore.State,
        ViewportStore.Message,
        Nothing,
        ViewportStore.Label,
        >

    override fun init(
        callbacks: Executor.Callbacks<
            ViewportStore.State,
            ViewportStore.Message,
            Nothing,
            ViewportStore.Label,
            >,
    ) {
        this.callbacks = callbacks
    }

    override fun executeIntent(intent: ViewportStore.Intent) {
        when (intent) {
            is ViewportStore.Intent.CameraIdle -> callbacks.onMessage(
                ViewportStore.Message.CameraSnapshotStored(intent.snapshot),
            )

            ViewportStore.Intent.CenterMarkerClicked -> callbacks.onMessage(
                ViewportStore.Message.CenterMarkerMenuOpened,
            )
            ViewportStore.Intent.CenterMarkerMenuDismissed -> callbacks.onMessage(
                ViewportStore.Message.CenterMarkerMenuDismissed,
            )

            ViewportStore.Intent.ViewportCommandConsumed -> callbacks.onMessage(
                ViewportStore.Message.PendingCommandUpdated(command = null),
            )

            ViewportStore.Intent.ZoomInClicked -> emitViewportCommand(MapViewportCommand.ZoomIn)
            ViewportStore.Intent.ZoomOutClicked -> emitViewportCommand(MapViewportCommand.ZoomOut)
        }
    }

    override fun executeAction(action: Nothing) = Unit

    override fun dispose() = Unit

    private fun emitViewportCommand(command: MapViewportCommand) {
        callbacks.onMessage(ViewportStore.Message.PendingCommandUpdated(command))
        callbacks.onLabel(ViewportStore.Label.ViewportCommandRequested(command))
    }
}
