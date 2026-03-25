package ru.tech.demomapapp.root.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.tech.demomapapp.feature.first.impl.DefaultFirstScreenComponent
import ru.tech.demomapapp.feature.first.impl.DefaultFirstScreenComponent.Output
import ru.tech.demomapapp.root.api.RootComponent

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.FirstScreen,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Config.FirstScreen -> RootComponent.Child.FirstScreen(
                component = DefaultFirstScreenComponent(
                    onOutput = ::onFirstScreenOutput,
                ),
            )
        }

    private fun onFirstScreenOutput(output: Output) {
        when (output) {
            Output.PrimaryActionClicked -> Unit
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object FirstScreen : Config
    }
}
