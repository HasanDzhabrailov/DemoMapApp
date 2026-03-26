package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>

    fun onPrimaryActionClick()

    data class Model(
        val mapState: MapState = MapState(
            styleUrl = "https://demotiles.maplibre.org/style.json",
        ),
    )
}
