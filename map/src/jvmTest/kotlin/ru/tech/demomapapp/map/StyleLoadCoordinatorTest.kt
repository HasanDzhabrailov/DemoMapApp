package ru.tech.demomapapp.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StyleLoadCoordinatorTest {

    @Test
    fun `same pending style reuses pending request`() {
        val coordinator = StyleLoadCoordinator()

        assertEquals(
            LoadRequestDecision.StartNew(cancelPending = false),
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = false),
        )

        assertEquals(
            LoadRequestDecision.AwaitPending,
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = false),
        )
    }

    @Test
    fun `loaded current style is reused without new request`() {
        val coordinator = StyleLoadCoordinator()

        assertEquals(
            LoadRequestDecision.StartNew(cancelPending = false),
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = false),
        )
        coordinator.onLoadCompleted("style-a")

        assertEquals(
            LoadRequestDecision.UseCurrent(cancelPending = false),
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = true),
        )
    }

    @Test
    fun `a to b to a rejects stale b callback and keeps current a`() {
        val coordinator = StyleLoadCoordinator()

        assertEquals(
            LoadRequestDecision.StartNew(cancelPending = false),
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = false),
        )
        coordinator.onLoadCompleted("style-a")

        assertEquals(
            LoadRequestDecision.StartNew(cancelPending = false),
            coordinator.onLoadRequested(styleUrl = "style-b", hasCurrentStyle = true),
        )

        assertEquals(
            LoadRequestDecision.UseCurrent(cancelPending = true),
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = true),
        )

        assertFalse(coordinator.shouldAcceptLoadedStyle("style-b"))

        assertEquals(
            LoadRequestDecision.UseCurrent(cancelPending = false),
            coordinator.onLoadRequested(styleUrl = "style-a", hasCurrentStyle = true),
        )
        assertTrue(coordinator.shouldAcceptLoadedStyle("style-a").not())
    }
}
