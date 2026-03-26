package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>

    fun onPrimaryActionClick()

    data class Model(
        val kicker: String = "Preview",
        val title: String = "Map preview",
        val description: String = "Shared UI hosts the screen while Android renders the map through a platform renderer.",
        val status: String = "Map renderer is isolated from screen state and business logic.",
        val mapStyleUrl: String = "https://demotiles.maplibre.org/style.json",
        val primaryActionTitle: String = "Primary action",
    )
}
