package ru.tech.demomapapp.feature.map.tools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapCatalogItemKind
import ru.tech.demomapapp.feature.map.api.MapLayerCatalog
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLayerSourceRef

class ToolsReducerTest {

    @Test
    fun `available map confirm adds overlay layer and opens loaded layers sheet`() {
        val selectedMap = MapLayerCatalog.items().first { it.kind == MapCatalogItemKind.OVERLAY_LAYER }
        val initialState = ToolsStore.State(
            selectedAvailableMap = selectedMap,
        )

        val updatedState = reduce(initialState, ToolsStore.Message.AvailableMapConfirmed)

        assertEquals(1, updatedState.layers.size)
        assertEquals(selectedMap.title, updatedState.layers.single().title)
        assertEquals(null, updatedState.selectedAvailableMap)
    }

    @Test
    fun `overlay layer move up follows visible stack order`() {
        val source = MapLayerSourceRef.RasterTileTemplate(templateId = "dem-overlay")
        val bottomLayer = MapLayerEntry(id = "bottom", title = "Bottom", source = source)
        val topLayer = MapLayerEntry(id = "top", title = "Top", source = source)
        val initialState = ToolsStore.State(
            layers = listOf(bottomLayer, topLayer),
            selectedOverlayLayer = bottomLayer,
        )

        val updatedState = reduce(initialState, ToolsStore.Message.LayerMovedUp)

        assertEquals(listOf("top", "bottom"), updatedState.layers.map(MapLayerEntry::id))
        assertEquals("bottom", updatedState.selectedOverlayLayer?.id)
    }

    @Test
    fun `overlay layer removed clears selection`() {
        val source = MapLayerSourceRef.RasterTileTemplate(templateId = "dem-overlay")
        val layer = MapLayerEntry(id = "overlay", title = "Overlay", source = source)
        val initialState = ToolsStore.State(
            layers = listOf(layer),
            selectedOverlayLayer = layer,
        )

        val updatedState = reduce(initialState, ToolsStore.Message.LayerRemoved)

        assertTrue(updatedState.layers.isEmpty())
        assertEquals(null, updatedState.selectedOverlayLayer)
        assertEquals(null, updatedState.editingOverlayOpacityLayer)
    }

    @Test
    fun `overlay layer opacity change updates layer and editor selection`() {
        val layer = MapLayerCatalog.items().first { it.kind == MapCatalogItemKind.OVERLAY_LAYER }
        val source = layer.source as MapLayerSourceRef.RasterTileTemplate
        val initialLayer = MapLayerEntry(
            id = layer.id,
            title = layer.title,
            source = source,
            opacity = 0.5f,
        )
        val initialState = ToolsStore.State(
            layers = listOf(initialLayer),
            editingOverlayOpacityLayer = initialLayer,
        )

        val updatedState = reduce(initialState, ToolsStore.Message.LayerOpacityChanged(0.8f))

        assertEquals(0.8f, updatedState.layers.single().opacity)
        assertEquals(0.8f, updatedState.editingOverlayOpacityLayer?.opacity)
    }

    @Test
    fun `available maps selection updates selected item`() {
        val selectedMap = MapLayerCatalog.items().first { it.kind == MapCatalogItemKind.BASE_MAP }

        val updatedState = reduce(
            ToolsStore.State(),
            ToolsStore.Message.AvailableMapSelected(selectedMap.id),
        )

        assertEquals(selectedMap.id, updatedState.selectedAvailableMap?.id)
    }

    private fun reduce(state: ToolsStore.State, message: ToolsStore.Message): ToolsStore.State =
        ToolsReducer.reduce(state, message)
}
