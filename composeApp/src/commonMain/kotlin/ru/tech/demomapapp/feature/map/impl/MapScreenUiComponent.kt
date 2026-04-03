package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.impl.location.LocationComponent
import ru.tech.demomapapp.feature.map.impl.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.impl.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.impl.viewport.ViewportComponent

internal interface MapScreenUiComponent : MapScreenComponent {
    val drawingComponent: DrawingComponent
    val locationComponent: LocationComponent
    val rulerComponent: RulerComponent
    val toolsComponent: ToolsComponent
    val viewportComponent: ViewportComponent
}
