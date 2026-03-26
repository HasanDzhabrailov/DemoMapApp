package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapState
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
            mapState = MapState(
                styleUrl = "https://demotiles.maplibre.org/style.json",
            ),
        )
}
