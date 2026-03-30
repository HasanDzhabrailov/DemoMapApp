package ru.tech.demomapapp.feature.map.impl.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

internal interface MapStore : Store<MapStore.Intent, MapStore.State, MapStore.Label> {
    sealed interface Intent {
        data class SyncModel(val model: MapScreenComponent.Model) : Intent
    }

    data class State(
        val model: MapScreenComponent.Model = MapScreenComponent.Model(),
    )

    sealed interface Label
}
