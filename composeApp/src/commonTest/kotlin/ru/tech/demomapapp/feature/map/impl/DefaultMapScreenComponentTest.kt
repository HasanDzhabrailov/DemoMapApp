package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent

class DefaultMapScreenComponentTest {

    @Test
    fun `point click opens info window from component state`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onFeatureClick(
            featureKey = point.id,
            featureType = MapScreenComponent.FeatureType.POINT,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        assertEquals(
            MapScreenComponent.FeatureInfoWindow(
                title = "Test point",
                createdAtText = "26.03.2026 10:00",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
            ),
            component.model.value.selectedFeatureInfoWindow,
        )
    }

    @Test
    fun `camera idle clears visible feature info window`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onFeatureClick(
            featureKey = point.id,
            featureType = MapScreenComponent.FeatureType.POINT,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        component.onCameraIdle(
            MapCameraSnapshot(
                latitude = 55.76,
                longitude = 37.62,
                zoom = 12.0,
                bearing = 0.0,
            ),
        )

        assertNull(component.model.value.selectedFeatureInfoWindow)
    }

    @Test
    fun `line drawing requires two fixed vertices before details can open`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreateLineClick()

        component.onDrawingDetailsClick()
        assertFalse(component.model.value.isCreateShapeSheetVisible)

        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        assertFalse(component.model.value.isCreateShapeSheetVisible)

        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()

        assertTrue(component.model.value.isCreateShapeSheetVisible)
    }

    @Test
    fun `line creation stores line and closes drawing flow`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreateLineClick()
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        component.onCreateShapeTitleChange("Route A")
        component.onCreateShapeConfirm()

        val line = component.model.value.mapState.lines.single()
        assertEquals("Route A", line.title)
        assertEquals(2, line.vertices.size)
        assertNull(component.model.value.shapeDrawingDraft)
        assertNull(component.model.value.drawingMode)
        assertFalse(component.model.value.isCreateShapeSheetVisible)
    }

    @Test
    fun `polygon creation stores polygon and can open shared info window`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePolygonClick()
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.77, longitude = 37.63))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        component.onCreateShapeTitleChange("Area A")
        component.onCreateShapeConfirm()

        val polygon = component.model.value.mapState.polygons.single()
        assertEquals("Area A", polygon.title)
        assertEquals(3, polygon.vertices.size)

        component.onFeatureClick(
            featureKey = polygon.id,
            featureType = MapScreenComponent.FeatureType.POLYGON,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 140, screenY = 260),
        )

        assertEquals(
            MapScreenComponent.FeatureInfoWindow(
                title = "Area A",
                createdAtText = "26.03.2026 10:00",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 140, screenY = 260),
            ),
            component.model.value.selectedFeatureInfoWindow,
        )
    }

    private fun createComponent(): DefaultMapScreenComponent {
        var nextId = 0
        return DefaultMapScreenComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            timeProvider = TimeProvider { 1_774_986_400_000L },
            featureIdProvider = FeatureIdProvider {
                nextId += 1
                "feature-$nextId"
            },
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(
                createdAtFormatter = MapPointCreatedAtFormatter { "26.03.2026 10:00" },
            ),
        )
    }

    private fun defaultSnapshot(
        latitude: Double = 55.75,
        longitude: Double = 37.61,
    ): MapCameraSnapshot =
        MapCameraSnapshot(
            latitude = latitude,
            longitude = longitude,
            zoom = 12.0,
            bearing = 0.0,
        )
}
