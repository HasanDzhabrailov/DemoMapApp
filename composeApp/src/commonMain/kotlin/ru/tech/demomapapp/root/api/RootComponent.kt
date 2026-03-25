package ru.tech.demomapapp.root.api

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.first.api.FirstScreenComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class FirstScreen(val component: FirstScreenComponent) : Child
    }
}
