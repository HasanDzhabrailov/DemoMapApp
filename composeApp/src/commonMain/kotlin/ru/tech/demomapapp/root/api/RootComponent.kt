package ru.tech.demomapapp.root.api

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class MapScreen(val instance: MapScreenComponent) : Child
    }
}
