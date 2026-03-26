package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

class DefaultMapScreenComponentTest {

    @Test
    fun `point click opens info window from component state`() {
        val component = createComponent()

        component.onCameraIdle(
            MapCameraSnapshot(
                latitude = 55.75,
                longitude = 37.61,
                zoom = 12.0,
                bearing = 0.0,
            ),
        )
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onPointClick(
            pointKey = point.id,
            anchor = MapScreenComponent.PointInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        assertEquals(
            MapScreenComponent.PointInfoWindow(
                title = "Test point",
                createdAtText = "26.03.2026 10:00",
                anchor = MapScreenComponent.PointInfoWindowAnchor(screenX = 120, screenY = 240),
            ),
            component.model.value.selectedPointInfoWindow,
        )
    }

    @Test
    fun `camera idle clears visible point info window`() {
        val component = createComponent()

        component.onCameraIdle(
            MapCameraSnapshot(
                latitude = 55.75,
                longitude = 37.61,
                zoom = 12.0,
                bearing = 0.0,
            ),
        )
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onPointClick(
            pointKey = point.id,
            anchor = MapScreenComponent.PointInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        component.onCameraIdle(
            MapCameraSnapshot(
                latitude = 55.76,
                longitude = 37.62,
                zoom = 12.0,
                bearing = 0.0,
            ),
        )

        assertNull(component.model.value.selectedPointInfoWindow)
    }

    private fun createComponent(): DefaultMapScreenComponent =
        DefaultMapScreenComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            timeProvider = TimeProvider { 1_774_986_400_000L },
            pointIdProvider = PointIdProvider { "point-1" },
            pointInfoWindowStateMapper = DefaultMapPointInfoWindowStateMapper(
                createdAtFormatter = MapPointCreatedAtFormatter { "26.03.2026 10:00" },
            ),
        )
}
