# Tasklist — MAP-API-002

## Phase 1: Analysis
- [x] Read PRD
- [x] Read plan
- [ ] List all 20+ fields in current MapScreenComponent.Model
- [ ] Classify each field:
  - [ ] CROSS_FEATURE (needed by multiple features)
  - [ ] CHILD_PRIVATE (one feature only)
  - [ ] RENDER_DATA (for MapRenderer)
  - [ ] DERIVED (can be computed)
- [ ] Document which UI components use which fields
- [ ] Identify render data requirements for MapRenderer

## Phase 2: Design
- [ ] Design new parent Model (max 7 cross-feature fields)
- [ ] Design OverlayType enum for overlay coordination
- [ ] Design MapRenderData structure
- [ ] Design UI multi-subscription pattern
- [ ] Review against Decompose best practices

## Phase 3: Store Refactoring
- [ ] Create new parent Model with cross-feature fields only
- [ ] Refactor MapRouterStore.State (remove child-specific state)
- [ ] Remove ChildState.* nested classes if not needed
- [ ] Update MapRouterReducer for new state structure
- [ ] Update MapRouterExecutor for new intents
- [ ] Remove or simplify State.toModel() method

## Phase 4: UI Refactoring
- [ ] Update MapScreenContent to subscribe to parent model
- [ ] Add subscriptions to child models via narrow interfaces
- [ ] Implement render data composition from multiple sources
- [ ] Update LocationControls to receive LocationModel directly
- [ ] Update DrawingContent to receive DrawingModel directly
- [ ] Update RulerOverlay to receive RulerModel directly
- [ ] Update ToolsOverlay to receive ToolsModel directly
- [ ] Update ViewportControls to receive ViewportModel directly

## Phase 5: Verification
- [ ] Verify parent Model has ≤ 7 fields
- [ ] Verify no child-private state in parent Model
- [ ] Verify UI subscribes to multiple models
- [ ] Compile check
- [ ] Run unit tests
- [ ] Run JVM tests
- [ ] Manual testing: all features work
- [ ] Line count verification: host component
- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| Parent Model ≤ 7 fields | PENDING |
| No child-private state in parent | PENDING |
| UI multi-subscription pattern | PENDING |
| No state duplication | PENDING |
| All features work | PENDING |
| Tests pass | PENDING |

## Design Decisions Log
| Decision | Rationale | Date |
|----------|-----------|------|
| Keep child state in children | Decompose component boundaries | - |
| UI subscribes to multiple models | Avoids "god model" anti-pattern | - |
| RenderData in parent | MapRenderer needs aggregated data | - |
| OverlayType enum | Centralizes overlay coordination | - |
