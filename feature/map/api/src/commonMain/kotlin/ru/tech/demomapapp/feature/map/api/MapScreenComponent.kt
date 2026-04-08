package ru.tech.demomapapp.feature.map.api

import com.arkivanov.decompose.value.Value

interface MapScreenComponent {
    val model: Value<Model>

    /**
     * Cross-feature coordination state only.
     * Child-private state is owned by respective child components.
     * UI subscribes to multiple models via MapScreenUiContract child contracts.
     */
    data class Model(
        val isRulerEnabled: Boolean = false,
        val pendingViewportCommand: MapViewportCommand? = null,
        val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    )

    enum class DrawingMode {
        LINE,
        POLYGON,
    }

    enum class FeatureType {
        POINT,
        LINE,
        POLYGON,
    }

    data class CreatePointDraft(
        val latitudeInput: String,
        val longitudeInput: String,
        val titleInput: String = "",
    )

    data class ShapeDrawingDraft(
        val mode: DrawingMode,
        val fixedVertices: List<MapVertex> = emptyList(),
        val titleInput: String = "",
    )

    data class FeatureInfoWindow(
        val title: String,
        val createdAtText: String,
        val anchor: FeatureInfoWindowAnchor,
    )

    data class FeatureInfoWindowAnchor(
        val screenX: Int,
        val screenY: Int,
    )
}
