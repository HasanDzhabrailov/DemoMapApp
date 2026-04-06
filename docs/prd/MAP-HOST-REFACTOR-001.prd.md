# MAP-HOST-REFACTOR-001 — Decouple DefaultMapHostComponent from cross-feature orchestration

## Context
- `DefaultMapHostComponent` (383 lines) has become a central orchestration/glue class with multiple responsibilities:
  1. Creates and wires 5 child components via `childContext()` (lines 53-139)
  2. Proxies ~20 event handler methods to children (lines 149-299)
  3. Manually synchronizes child state via `sync*State()` methods (lines 302-351)
  4. Routes viewport commands between features
  5. Contains business rules like blocking center marker during drawing mode (lines 223-229)
- This violates AGENTS.md rules: Component should own only lifecycle and wiring, not business logic or cross-feature coordination
- Child state is duplicated into `MapRouterStore` and synchronized manually

## Goal
Remove cross-feature orchestration from DefaultMapHostComponent and move to bounded parent store/executor:
1. Extract overlay exclusivity rules
2. Remove viewport command routing glue
3. Remove component-level business rules
4. Eliminate manual state synchronization

## Non-Goals
- No visual redesign of map overlays
- No changes to user-visible behavior
- No changes to child feature stores (drawing, location, ruler, tools, viewport)
- No changes to map rendering

## User Scenarios
- Host Component focuses only on lifecycle, Store creation, and navigation wiring
- Cross-feature behavior (overlay dismissal, command routing) owned by store/executor
- Business rules (drawing mode blocking) in testable store layer
- Manual state sync eliminated

## Acceptance Criteria
### Overlay Exclusivity
- [ ] `routeOverlayInteraction()` and overlay dismissal logic moved from host to store/executor
- [ ] Host delegates to store instead of manual coordination
- [ ] No `DismissToolsMenu`, `DismissViewportMenu` labels handled in host

### Viewport Command Routing
- [ ] `onViewportCommandRequested()` methods removed from host
- [ ] Command source tracking lives in store state, not host fields
- [ ] Commands flow through store, not host orchestration

### Business Rules
- [ ] `onCenterMarkerClick()` has no conditional blocking logic (lines 223-229)
- [ ] Drawing mode check lives in store/executor
- [ ] Behavior preserved: center marker disabled during drawing

### State Synchronization
- [ ] All `syncToolsState()`, `syncDrawingState()`, `syncLocationState()`, `syncRulerState()`, `syncViewportState()` methods removed
- [ ] No manual state mirroring from children to router store
- [ ] Host line count reduced from 383 to <150 lines

### General
- [ ] Project compiles and tests pass
- [ ] No behavioral regression in any feature

## Constraints
- Follow `AGENTS.md`
- Component -> Store -> UI pattern preserved
- One Component = one Store
- Reducer pure, Executor handles side effects
- Keep dependencies explicit via constructors

## Success Metrics
- DefaultMapHostComponent < 150 lines (currently 383)
- No manual state sync methods
- No business logic in component methods
- All cross-feature coordination in store/executor
- Tests pass

## Open Questions
1. Should overlay state be part of existing MapRouterStore or separate bounded store?
2. How to eliminate state sync without breaking existing UI that reads from Model?
3. Should command consumption be UI-side or store-side responsibility?
4. Alternative: Use state-driven UI (center marker disabled state) vs intent rejection?
