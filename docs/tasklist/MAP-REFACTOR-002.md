# Tasklist - MAP-REFACTOR-002

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

- [ ] Create `feature/map/impl/drawing/` package structure
- [ ] Create `DrawingComponent` interface with Output callback
- [ ] Create `DrawingModel` data class
- [ ] Create `DrawingStore` interface (State/Intent/Label)
- [ ] Create `DrawingStore.State` with points/lines/polygons/drafts
- [ ] Create `DrawingStore.Intent` enum/sealed class
- [ ] Create `DrawingStore.Label` for FeatureCreated
- [ ] Create `DrawingExecutor` (use case calls, side effects)
- [ ] Create `DrawingReducer` (pure)
- [ ] Create `DrawingStoreFactory`
- [ ] Create `DefaultDrawingComponent` (wires store + output)
- [ ] Migrate logic from `DrawingHandler`
- [ ] Migrate logic from `CreatePointHandler`

- [ ] Unit test: CreatePoint flow
- [ ] Unit test: CreateLine flow
- [ ] Unit test: CreatePolygon flow
- [ ] Unit test: Draft updates
- [ ] Unit test: Output callback called on FeatureCreated

- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| DrawingComponent created | PENDING |
| DrawingStore works | PENDING |
| Output callback explicit | PENDING |
| Use cases in Executor | PENDING |
| Tests pass | PENDING |
