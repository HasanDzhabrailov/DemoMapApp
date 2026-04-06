# Plan — MAP-HOST-REFACTOR-001

## Summary
Comprehensive refactor of DefaultMapHostComponent to remove cross-feature orchestration and move logic to store/executor layer.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host/DefaultMapHostComponent.kt` (major refactor)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterStore.kt` (add intents/state)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterExecutor.kt` (add logic)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterReducer.kt` (handle new messages)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host/MapHostRouterStateMappers.kt` (may be reduced/removed)

## Implementation Notes

### Phase 1: Analyze Current State
1. Document all `sync*State()` methods and what they synchronize
2. Document all overlay dismissal triggers
3. Document viewport command flow
4. Document business rules in component

### Phase 2: Design Store Changes
1. Add overlay state to MapRouterStore.State
2. Add intents for overlay management: `OverlayOpened`, `OverlayDismissed`, `DismissConflictingOverlays`
3. Add command routing intents: `ViewportCommandIssued`, `ViewportCommandConsumed`
4. Add business rule state: `isCenterMarkerEnabled`
5. Design how to eliminate manual state sync (options below)

### Phase 3: Eliminate State Sync (Critical)
**Option A - Child State via Labels:**
- Children emit state changes as Labels
- Parent Store subscribes to child Labels
- No manual sync methods needed

**Option B - Direct Store Communication:**
- Children have reference to parent Store
- Children send intents directly to parent
- More coupling, less Decompose-idiomatic

**Option C - Read Child Model Directly:**
- Parent Store has access to child models
- Maps child models to own state in reducer
- No explicit sync methods, reactive mapping

**Recommendation:** Start with Option C (least invasive)

### Phase 4: Move Overlay Logic
1. Add `OverlayCoordinator` utility in executor or inline logic
2. When overlay opened, executor calculates what to dismiss
3. Executor emits labels for dismissal actions
4. Host reacts to labels (minimal delegation)

### Phase 5: Move Command Routing
1. Store tracks pending command and source
2. Executor handles command lifecycle
3. Host only passes command to renderer, doesn't track source

### Phase 6: Move Business Rules
1. Store calculates `isCenterMarkerEnabled` based on drawing mode
2. Either: UI shows disabled state, OR store rejects intent
3. Component has no conditional logic

### Phase 7: Refactor Host
1. Remove all `sync*State()` methods
2. Remove `onViewportCommandRequested()` orchestration
3. Remove business rule checks
4. Keep: child creation, lifecycle wiring, label handling

## Risks
- Risk 1: State sync elimination may break existing tests
- Risk 2: Timing changes in overlay dismissal
- Risk 3: UI may need updates for disabled states
- Risk 4: Large refactor, high regression risk

## Mitigation
- Do not change child feature implementations
- Keep existing interfaces
- Add tests before refactoring (characterization tests)
- Commit after each phase

## Verification
- `./gradlew :composeApp:test` - all existing tests pass
- `./gradlew :composeApp:jvmTest` - JVM tests pass
- Manual testing: overlay interactions, viewport commands, drawing mode
- Line count check: host < 150 lines

## Task Breakdown
1. [ ] Analyze and document current sync/overlay/command patterns
2. [ ] Design store state changes (overlay, commands, rules)
3. [ ] Add new intents and messages to MapRouterStore
4. [ ] Implement overlay coordination in executor
5. [ ] Implement command routing in executor
6. [ ] Implement business rules in store/executor
7. [ ] Eliminate sync*State() methods from host
8. [ ] Refactor host to thin delegation layer
9. [ ] Run all tests and fix regressions
10. [ ] Manual testing of all features
