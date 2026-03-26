package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

class DefaultMapScreenComponent(
    private val onOutput: (Output) -> Unit,
) : MapScreenComponent {

    override val model: Value<MapScreenComponent.Model> = MutableValue(defaultModel())

    override fun onPrimaryActionClick() {
        onOutput(Output.PrimaryActionClicked)
    }

    sealed interface Output {
        data object PrimaryActionClicked : Output
    }

    private fun defaultModel(): MapScreenComponent.Model =
        MapScreenComponent.Model(
            kicker = "DemoMapApp",
            title = "Map screen",
            description = "The shared screen owns layout only while the Android source set hosts the MapLibre view integration.",
            status = "The map layer renders tiles only and stays separate from business decisions.",
            primaryActionTitle = "Continue",
        )
}
