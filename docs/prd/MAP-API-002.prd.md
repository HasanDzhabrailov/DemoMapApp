# MAP-API-002 — Separate cross-feature state from child-private state

## Context
- `MapScreenComponent.Model` has 20+ fields, aggregating state from all child features
- This creates tight coupling between features through shared model
- AGENTS.md suggests refactoring state if it has more than 7 top-level fields
- **Critical Issue**: Current approach in plan suggested consolidating all state into parent Model, which creates a "god model" violating Decompose component boundaries

## Goal
Restructure state ownership to respect component boundaries:
1. **Parent Model**: Contains only cross-feature coordination state (overlay visibility, active feature, etc.)
2. **Child Models**: Each child component owns its private state (location, drawing, ruler, tools, viewport)
3. **UI Access**: Subscribes to multiple models via narrow interfaces, not single aggregated model

## Non-Goals
- No moving child-private state to parent (violates component boundaries)
- No changes to child feature stores internal logic
- No changes to UI behavior or visual appearance
- No creation of "god model" with all state

## User Scenarios
- Features maintain their own state boundaries (Decompose-idiomatic)
- Cross-feature coordination through minimal parent state
- UI composes views from multiple state sources
- Each component lifecycle manages its own state

## State Ownership Classification

### Cross-Feature State (Parent Model)
State needed for coordination between features:
- `activeOverlay` - which overlay is currently visible (tools, drawing, center marker)
- `pendingViewportCommand` - command from any child to viewport
- `selectedFeatureInfoWindow` - currently selected map feature
- `isRulerEnabled` - affects multiple features (viewport, drawing)

### Child-Private State (Stays in Children)
State owned by specific feature:
- **Location**: `myLocationMode`, `currentLocationMarker`, `pendingRequest`
- **Drawing**: `points`, `lines`, `polygons`, `drawingMode`, `shapeDraft`
- **Ruler**: `measurement`, `infoWindow`
- **Tools**: `layers`, `selectedMapStyle`, `availableCatalog`
- **Viewport**: `cameraSnapshot`, `isCenterMarkerMenuVisible`

## Solution Approach

### Phase 1: Audit and Classify
1. Document all 20+ Model fields
2. Classify each as cross-feature or child-private
3. Identify which UI components need which state

### Phase 2: Restructure Parent Model
Reduce parent Model to 5-7 cross-feature fields:
```kotlin
data class Model(
    val activeOverlay: OverlayType? = null,
    val pendingViewportCommand: MapViewportCommand? = null,
    val selectedFeatureInfoWindow: FeatureInfoWindow? = null,
    val isRulerEnabled: Boolean = false,
    // Cross-feature render data only
    val renderData: MapRenderData? = null,
)
```

### Phase 3: UI Multi-Subscription Pattern
UI subscribes to multiple state sources via narrow interfaces:
```kotlin
@Composable
fun MapScreenContent(component: MapScreenUiContract) {
    // Parent state for cross-feature coordination
    val parentModel by component.model.subscribeAsState()
    
    // Child states via narrow interfaces
    val locationModel by component.locationUi.model.subscribeAsState()
    val drawingModel by component.drawingUi.model.subscribeAsState()
    val rulerModel by component.rulerUi.model.subscribeAsState()
    val toolsModel by component.toolsUi.model.subscribeAsState()
    
    // Compose UI from multiple sources
}
```

### Benefits
- ✅ Respects Decompose component boundaries
- ✅ Each component owns its lifecycle and state
- ✅ No "god model" anti-pattern
- ✅ Better testability (test components in isolation)
- ✅ Easier module extraction (clear state boundaries)

## Acceptance Criteria
- [ ] Parent Model has ≤ 7 top-level fields (cross-feature only)
- [ ] Child-private state removed from parent Model
- [ ] UI subscribes to child models via narrow interfaces (from MAP-API-001)
- [ ] No `sync*State()` methods (already removed in MAP-HOST-REFACTOR-001)
- [ ] No regression in UI behavior
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Respect Decompose component boundaries
- State nesting depth ≤ 2
- No heavy derived data in State
- **CRITICAL**: Do not create "god model" with all state

## Success Metrics
- Parent Model field count ≤ 7
- Child components own their private state
- UI subscribes to multiple models
- No state duplication between parent and children
- Tests pass

## Dependencies
- Must be done after MAP-API-001 (narrow interfaces provide model access)
- Must be done after MAP-HOST-REFACTOR-001 (state sync removed)

## Decompose Alignment
> "Each component has its own lifecycle, which is automatically managed by Decompose. So everything encapsulated by a component is scoped."

This approach keeps state scoped to components, not centralized in parent.

## Open Questions
1. Should render data (points, lines, polygons) be in parent for MapRenderer or passed differently?
2. How to handle backward compatibility during transition?
3. Should viewport camera state be cross-feature or viewport-private?
