# MAP-API-001 — Narrow map API to hide internal child components

## Context
- `feature.map.api.MapScreenUiContract` exposes `DrawingComponent`, `LocationComponent`, `RulerComponent`, `ToolsComponent` via `MapScreenChildComponents`.
- API package knows internal child structure, weakening future module extraction.
- AGENTS.md requires API to contain only minimal interfaces and entry points.
- **Critical Issue:** `MapScreenContent` currently accesses child components directly:
  ```kotlin
  LocationControls(component = component.locationComponent)
  DrawingContent(component = component.drawingComponent)
  RulerOverlay(component = component.rulerComponent)
  ToolsOverlay(component = component.toolsComponent)
  ```

## Goal
Narrow map API to stable screen contract and hide child component implementations.

## Non-Goals
- No changes to child component implementations
- No visual changes to UI
- No changes to business logic

## User Scenarios
- API consumers depend only on stable contracts
- Internal child components can change without affecting API consumers
- Future module extraction is easier
- UI continues to work without direct component access

## UI Access Problem Analysis

Current UI accesses child components for:
1. **LocationControls** - needs location state and callbacks
2. **DrawingContent** - needs drawing state and slot navigation
3. **RulerOverlay** - needs ruler state
4. **ToolsOverlay** - needs tools state and slot navigation

### Solution Options

#### Option A: Composition via Slots (Decompose-idiomatic)
- Parent component provides `ChildSlot<*, LocationChild>` instead of `LocationComponent`
- UI renders slot content via `Children()` composable
- Child component created via slot navigation
- **Pros:** Standard Decompose pattern, clean separation
- **Cons:** Requires refactoring child components to use slots

#### Option B: State-Driven UI (Recommended)
- Move all child state into parent Model
- UI reads flat state from Model
- UI emits intents that flow to appropriate child
- **Pros:** Simple, no component exposure, easy to test
- **Cons:** May duplicate some child state in parent

#### Option C: Narrow Interfaces
- Replace concrete `LocationComponent` with minimal `LocationUiContract` interface
- Interface exposes only what UI needs: `val model: Value<LocationModel>`
- Implementation stays internal
- **Pros:** Preserves current structure, just hides types
- **Cons:** Still exposes component interface

#### Option D: Child Component Factory
- Parent provides factory: `(ComponentContext) -> LocationUi`
- UI creates child component when needed
- **Pros:** Lazy creation
- **Cons:** Complex, not Decompose-idiomatic

## Recommendation

**Use Option C (Narrow Interfaces) as Phase 1**, then migrate to Option B (State-Driven) in MAP-API-002.

### Phase 1: Narrow Interfaces (This Ticket)
1. Create minimal interfaces for each child:
   ```kotlin
   interface LocationUiContract {
       val model: Value<LocationModel>
       fun onGpsToggle()
       fun onMyLocationClick()
       // ... only what UI needs
   }
   ```
2. Child components implement these interfaces
3. `MapScreenChildComponents` exposes interfaces, not concrete types
4. API package contains only interfaces

### Phase 2: State-Driven (MAP-API-002)
1. Consolidate all state into parent Model
2. UI reads state directly
3. Remove child component exposure entirely

## Acceptance Criteria

### Phase 1 (This Ticket)
- [ ] Create `LocationUiContract`, `DrawingUiContract`, `RulerUiContract`, `ToolsUiContract` interfaces
- [ ] Interfaces contain only UI-relevant methods (model + callbacks)
- [ ] Child components implement these interfaces
- [ ] `MapScreenChildComponents` exposes interfaces, not concrete types
- [ ] API package has no imports from internal child areas
- [ ] `MapScreenContent` compiles without changes (uses interfaces)
- [ ] Project compiles and tests pass

### Explicit Non-Goals for This Ticket
- [ ] Do NOT move state to parent Model yet (that's MAP-API-002)
- [ ] Do NOT change UI implementation (uses same pattern)
- [ ] Do NOT change child component logic

## Constraints
- Follow `AGENTS.md`
- API contains only minimal interfaces and models
- Internal implementation details hidden
- Decompose patterns preserved

## Success Metrics
- API package has no imports from internal child areas
- `DrawingComponent`, `LocationComponent`, etc. not exposed in API
- Module boundary is clearer
- All existing tests pass

## Dependencies
- Should be done after MAP-HOST-REFACTOR-001 (host decoupling)
- Or can be done in parallel if host interface is stable

## Open Questions
1. Which methods belong in narrow interfaces vs internal only?
2. Should slot navigation be exposed or handled internally?
3. How to handle child-specific UI components (DrawingContent, etc.)?
