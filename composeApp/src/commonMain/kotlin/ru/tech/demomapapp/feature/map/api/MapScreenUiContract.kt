package ru.tech.demomapapp.feature.map.api

import ru.tech.demomapapp.feature.map.drawing.DrawingComponent
import ru.tech.demomapapp.feature.map.location.LocationComponent
import ru.tech.demomapapp.feature.map.ruler.RulerComponent
import ru.tech.demomapapp.feature.map.tools.ToolsComponent

interface MapScreenUiContract : MapScreenComponent {
    val drawingComponent: DrawingComponent
    val locationComponent: LocationComponent
    val rulerComponent: RulerComponent
    val toolsComponent: ToolsComponent
}
