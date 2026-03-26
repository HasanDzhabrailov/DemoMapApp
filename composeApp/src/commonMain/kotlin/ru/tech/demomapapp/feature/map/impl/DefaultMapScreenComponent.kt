package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

class DefaultMapScreenComponent(
    componentContext: ComponentContext,
) : MapScreenComponent, ComponentContext by componentContext {

    override val model: Value<MapScreenComponent.Model> = MutableValue(defaultModel())

    private fun defaultModel(): MapScreenComponent.Model =
        MapScreenComponent.Model(
            mapState = MapState(
                styleUrl = "https://demotiles.maplibre.org/style.json",
            ),
        )
}
