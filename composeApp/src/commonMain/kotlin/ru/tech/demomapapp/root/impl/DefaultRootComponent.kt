package ru.tech.demomapapp.root.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.tech.demomapapp.feature.map.host.DefaultMapScreenComponent
import ru.tech.demomapapp.root.api.RootComponent

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.MapScreen,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child = when (config) {
        Config.MapScreen -> RootComponent.Child.MapScreen(
            instance = DefaultMapScreenComponent(
                componentContext = componentContext,
            ),
        )
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object MapScreen : Config
    }
}
