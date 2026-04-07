package ru.tech.demomapapp.feature.map.api

/**
 * Child components exposed via narrow UI contracts.
 * This hides internal implementation details from API consumers.
 */
interface MapScreenChildComponents {
    val drawingUi: DrawingUiContract
    val locationUi: LocationUiContract
    val rulerUi: RulerUiContract
    val toolsUi: ToolsUiContract
    val viewportUi: ViewportUiContract
}

/**
 * MapScreenUiContract combines all host interfaces and exposes child components
 * via narrow UI contracts (LocationUiContract, DrawingUiContract, etc.).
 */
interface MapScreenUiContract :
    MapScreenComponent,
    MapScreenToolsHost,
    MapScreenLocationHost,
    MapScreenViewportHost,
    MapScreenDrawingHost,
    MapScreenFeatureHost,
    MapScreenChildComponents {
}
