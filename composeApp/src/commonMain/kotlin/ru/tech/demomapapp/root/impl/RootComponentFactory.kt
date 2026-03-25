package ru.tech.demomapapp.root.impl

import com.arkivanov.decompose.ComponentContext
import ru.tech.demomapapp.root.api.RootComponent

fun createRootComponent(componentContext: ComponentContext): RootComponent =
    DefaultRootComponent(componentContext = componentContext)
