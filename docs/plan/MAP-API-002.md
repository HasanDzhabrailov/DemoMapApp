# Plan — MAP-API-002

## Summary
Separate cross-feature state (parent Model) from child-private state (child models), with UI subscribing to multiple sources.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/MapScreenComponent.kt` - reduce Model fields
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/` - narrow interfaces (from MAP-API-001)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterStore.kt` - remove child state
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/ui/MapScreenContent.kt` - multi-subscription UI

## Implementation Notes

### Phase 1: State Audit
```kotlin
// Current Model (20+ fields) - document all
val mapState: MapState
val availableMapCatalog: List<MapCatalogItem>
val lastCameraSnapshot: MapCameraSnapshot?
val isMapToolsMenuVisible: Boolean
val isAvailableMapsSheetVisible: Boolean
// ... etc
```

Classify each field:
- **CROSS_FEATURE**: Needed by multiple features for coordination
- **CHILD_PRIVATE**: Only one feature uses this
- **RENDER_DATA**: Needed by MapRenderer (may need special handling)
- **DERIVED**: Can be computed from other fields

### Phase 2: Design New Parent Model
Target 5-7 fields for cross-feature coordination:
```kotlin
data class Model(
    // Overlay coordination
    val activeOverlay: OverlayType? = null,
    
    // Viewport command routing
    val pendingViewportCommand: MapViewportCommand? = null,
    
    // Feature selection (cross-feature)
    val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    
    // Ruler affects viewport and drawing
    val isRulerEnabled: Boolean = false,
    
    // Render data (special case - needed by MapRenderer)
    val renderData: MapRenderData? = null,
)

enum class OverlayType {
    TOOLS_MENU,
    TOOLS_AVAILABLE_MAPS,
    TOOLS_MAPS_ON_SCREEN,
    DRAWING_POINT_SHEET,
    DRAWING_SHAPE_SHEET,
    CENTER_MARKER_MENU,
}
```

### Phase 3: Design UI Multi-Subscription
```kotlin
@Composable
fun MapScreenContent(component: MapScreenUiContract) {
    // Cross-feature state from parent
    val model by component.model.subscribeAsState()
    
    // Child-private states via narrow interfaces
    val locationModel by component.locationUi.model.subscribeAsState()
    val drawingModel by component.drawingUi.model.subscribeAsState()
    val rulerModel by component.rulerUi.model.subscribeAsState()
    val toolsModel by component.toolsUi.model.subscribeAsState()
    val viewportModel by component.viewportUi.model.subscribeAsState()
    
    // Compose render model from multiple sources
    val renderModel = remember(drawingModel, locationModel, rulerModel) {
        MapRenderData(
            points = drawingModel.points,
            lines = drawingModel.lines,
            polygons = drawingModel.polygons,
            currentLocationMarker = locationModel.currentMarker,
            rulerMeasurement = rulerModel.measurement,
            // ...
        )
    }
    
    // Pass specific models to child UI components
    LocationControls(
        model = locationModel,
        onGpsToggle = component.locationUi::onGpsToggle,
        // ...
    )
    
    DrawingContent(
        model = drawingModel,
        pointSheetSlot = component.drawingUi.pointSheetSlot,
        // ...
    )
}
```

### Phase 4: Refactor Router Store
Remove child-specific state from `MapRouterStore.State`:
```kotlin
// BEFORE: State contains all child state
data class State(
    val viewportState: ChildState.Viewport? = null,
    val toolsState: ChildState.Tools? = null,
    val locationState: ChildState.Location? = null,
    // ... etc
)

// AFTER: State contains only cross-feature coordination
data class State(
    val activeOverlay: OverlayType? = null,
    val pendingViewportCommand: MapViewportCommand? = null,
    val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    val isRulerEnabled: Boolean = false,
)
```

Remove `toModel()` method that aggregates child state - UI will subscribe directly to children.

### Phase 5: Update Child Components
Ensure all child components expose their models via narrow interfaces:
```kotlin
// In narrow interface (api package)
interface LocationUiContract {
    val model: Value<LocationModel>
    // ... callbacks
}

// Implementation (impl package)
internal class DefaultLocationComponent : LocationComponent, LocationUiContract {
    override val model: Value<LocationModel> = holder.model
    // ...
}
```

## Risks
- **Risk 1**: UI refactoring complexity (multiple subscriptions)
- **Risk 2**: Performance concerns with multiple Value subscriptions
- **Risk 3**: Child component lifecycle management with UI subscriptions

## Risk Mitigation
1. Use `remember` to cache derived render data
2. Decompose's `Value` is optimized for subscriptions
3. Component lifecycle automatically managed by Decompose

## Verification
```bash
# Compile check
./gradlew :composeApp:compileDebugKotlin

# Tests
./gradlew :composeApp:test
./gradlew :composeApp:jvmTest

# Line count verification
grep -c "val \w*:" composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/MapScreenComponent.kt
# Should be ≤ 7
```

## Task Breakdown
1. [ ] Audit all Model fields and classify ownership
2. [ ] Design new parent Model (≤ 7 cross-feature fields)
3. [ ] Design UI multi-subscription pattern
4. [ ] Refactor MapRouterStore.State (remove child state)
5. [ ] Remove MapRouterStore.State.toModel() aggregation
6. [ ] Update MapScreenContent with multiple subscriptions
7. [ ] Update child UI components to receive models directly
8. [ ] Verify render data flow to MapRenderer
9. [ ] Run tests and fix regressions
