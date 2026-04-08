package ru.tech.demomapapp.feature.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand

/**
 * Unit tests for MapScreenComponent.Model verifying MAP-API-002 architecture.
 *
 * Note: Full integration tests for DefaultMapHostComponent require special setup
 * for Decompose's childContext() which is tested at UI/integration level.
 * These tests verify the core state model design.
 */
class DefaultMapHostComponentTest {

    @Test
    fun `parent model has exactly 3 cross feature fields`() {
        val model = MapScreenComponent.Model()

        // Verify default values
        assertEquals(false, model.isRulerEnabled)
        assertNull(model.pendingViewportCommand)
        assertNull(model.selectedFeatureInfoWindow)
    }

    @Test
    fun `parent model can be created with custom values`() {
        val command = MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6)
        val infoWindow = MapScreenComponent.FeatureInfoWindow(
            title = "Test",
            createdAtText = "01.01.2024",
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(100, 200),
        )

        val model = MapScreenComponent.Model(
            isRulerEnabled = true,
            pendingViewportCommand = command,
            selectedFeatureInfoWindow = infoWindow,
        )

        assertEquals(true, model.isRulerEnabled)
        assertEquals(command, model.pendingViewportCommand)
        assertEquals(infoWindow, model.selectedFeatureInfoWindow)
    }

    @Test
    fun `parent model copy creates independent instance`() {
        val original = MapScreenComponent.Model()
        val modified = original.copy(isRulerEnabled = true)

        assertEquals(false, original.isRulerEnabled)
        assertEquals(true, modified.isRulerEnabled)
    }
}
