package ru.tech.demomapapp.feature.map.tools

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.ToolsModel
import ru.tech.demomapapp.feature.map.api.ToolsUiContract

class DefaultToolsComponentTest {

    @Test
    fun `component forwards layers changed label to output`() {
        val output = TestOutput()
        val component = DefaultToolsComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            toolsStoreFactory = ToolsStoreFactory(),
            initialModel = ToolsModel(),
            output = output,
        )

        component.onAvailableMapsClick()
        component.onAvailableMapSelect("google-overlay")
        component.onAvailableMapConfirm()

        assertEquals(component.model.value.layers, output.layers.single())
        assertTrue(component.childSlot.value.child?.instance is ToolsUiContract.Child.MapsOnScreen)
    }

    private class TestOutput : ToolsComponent.Output {
        val layers = mutableListOf<List<MapLayerEntry>>()

        override fun onStateChanged() = Unit

        override fun onLayersChanged(layers: List<MapLayerEntry>) {
            this.layers += layers
        }
    }
}
