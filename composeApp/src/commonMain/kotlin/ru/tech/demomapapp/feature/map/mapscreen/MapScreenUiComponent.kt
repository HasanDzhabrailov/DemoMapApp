package ru.tech.demomapapp.feature.map.mapscreen

import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.tools.ToolsComponent
import ru.tech.demomapapp.feature.map.impl.viewport.ViewportComponent

internal interface MapScreenUiComponent : MapScreenComponent {
    val drawingComponent: DrawingComponent
    val locationComponent: LocationComponent
    val rulerComponent: RulerComponent
    val toolsComponent: ToolsComponent
    val viewportComponent: ViewportComponent
}
