package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

internal class MapStoreFactory(
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) {
    fun create(initialState: MapStore.State = MapStore.State()): MapStore =
        object : MapStore, Store<MapStore.Intent, MapStore.State, MapStore.Label> by storeFactory.create(
            name = "MapStore",
            initialState = initialState,
            bootstrapper = null,
            executorFactory = ::ExecutorImpl,
            reducer = MapStoreReducer,
        ) {}

    private class ExecutorImpl :
        com.arkivanov.mvikotlin.core.store.Executor<
            MapStore.Intent,
            Nothing,
            MapStore.State,
            MapStoreMessage,
            MapStore.Label,
        > {

        private lateinit var callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
            MapStore.State,
            MapStoreMessage,
            Nothing,
            MapStore.Label,
        >

        override fun init(
            callbacks: com.arkivanov.mvikotlin.core.store.Executor.Callbacks<
                MapStore.State,
                MapStoreMessage,
                Nothing,
                MapStore.Label,
            >,
        ) {
            this.callbacks = callbacks
        }

        override fun executeIntent(intent: MapStore.Intent) {
            when (intent) {
                is MapStore.Intent.CenterMarker.Clicked -> callbacks.onMessage(MapStoreMessage.CenterMarkerMenuOpened)
                is MapStore.Intent.CenterMarker.MenuDismissed -> callbacks.onMessage(MapStoreMessage.CenterMarkerMenuDismissed)
                is MapStore.Intent.CreatePoint.Clicked -> callbacks.onMessage(MapStoreMessage.CreatePointSheetOpened)
                is MapStore.Intent.CreatePoint.LatitudeChanged -> callbacks.onMessage(
                    MapStoreMessage.CreatePointLatitudeChanged(intent.value),
                )
                is MapStore.Intent.CreatePoint.LongitudeChanged -> callbacks.onMessage(
                    MapStoreMessage.CreatePointLongitudeChanged(intent.value),
                )
                is MapStore.Intent.CreatePoint.TitleChanged -> callbacks.onMessage(
                    MapStoreMessage.CreatePointTitleChanged(intent.value),
                )
                is MapStore.Intent.CreatePoint.SheetDismissed -> callbacks.onMessage(MapStoreMessage.CreatePointSheetDismissed)
                is MapStore.Intent.Drawing.CreateLineClicked -> callbacks.onMessage(
                    MapStoreMessage.DrawingStarted(MapStore.DrawingMode.LINE),
                )
                is MapStore.Intent.Drawing.CreatePolygonClicked -> callbacks.onMessage(
                    MapStoreMessage.DrawingStarted(MapStore.DrawingMode.POLYGON),
                )
                is MapStore.Intent.Drawing.DetailsClicked -> callbacks.onMessage(MapStoreMessage.ShapeSheetOpened)
                is MapStore.Intent.Drawing.Dismissed -> callbacks.onMessage(MapStoreMessage.DrawingDismissed)
                is MapStore.Intent.Drawing.ShapeSheetDismissed -> callbacks.onMessage(MapStoreMessage.ShapeSheetDismissed)
                is MapStore.Intent.Drawing.TitleChanged -> callbacks.onMessage(MapStoreMessage.ShapeTitleChanged(intent.value))
                is MapStore.Intent.FeatureSelection.FeatureInfoWindowDismissed -> {
                    callbacks.onMessage(MapStoreMessage.FeatureInfoWindowDismissed)
                }
                is MapStore.Intent.Location,
                is MapStore.Intent.Ruler,
                is MapStore.Intent.CreatePoint.Confirmed,
                is MapStore.Intent.Drawing.AddPositionClicked,
                is MapStore.Intent.Drawing.Confirmed,
                is MapStore.Intent.Drawing.RemoveLastPositionClicked,
                is MapStore.Intent.FeatureSelection.FeatureClicked,
                is MapStore.Intent.Viewport,
                -> Unit
                is MapStore.Intent.SyncState -> callbacks.onMessage(MapStoreMessage.StateSynced(intent.state))
                is MapStore.Intent.Tools.AvailableMapsClicked,
                is MapStore.Intent.Tools.MapToolsDismissed,
                is MapStore.Intent.Tools.MapsOnScreenClicked,
                -> callbacks.onMessage(MapStoreMessage.MapToolsMenuDismissed)
                is MapStore.Intent.Tools.MapToolsClicked -> callbacks.onMessage(MapStoreMessage.MapToolsMenuToggled)
            }
        }

        override fun executeAction(action: Nothing) = Unit

        override fun dispose() = Unit
    }
}
