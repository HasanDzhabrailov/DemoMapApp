package ru.tech.demomapapp.feature.first.impl

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.first.api.FirstScreenComponent

class DefaultFirstScreenComponent(
    private val onOutput: (Output) -> Unit,
) : FirstScreenComponent {

    override val model: Value<FirstScreenComponent.Model> = MutableValue(defaultModel())

    override fun onPrimaryActionClick() {
        onOutput(Output.PrimaryActionClicked)
    }

    sealed interface Output {
        data object PrimaryActionClicked : Output
    }

    private fun defaultModel(): FirstScreenComponent.Model =
        FirstScreenComponent.Model(
            kicker = "DemoMapApp",
            title = "First screen template",
            description = "The screen component is isolated from UI and reports user actions back to the root component.",
            status = "RootComponent owns navigation and child wiring.",
            primaryActionTitle = "Continue",
        )
}
