# Tasklist - MAP-REFACTOR-005

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

- [ ] Create `feature/map/impl/viewport/` package structure
- [ ] Create `ViewportComponent` interface with Output
- [ ] Create `ViewportModel` data class
- [ ] Create `ViewportStore` interface
- [ ] Create `ViewportStore.State`
- [ ] Create `ViewportStore.Intent`
- [ ] Create `ViewportStore.Label` (ViewportCommand)
- [ ] Create `ViewportExecutor`
- [ ] Create `ViewportReducer`
- [ ] Create `ViewportStoreFactory`
- [ ] Create `DefaultViewportComponent`

- [ ] Unit test: Zoom in генерирует ViewportCommand
- [ ] Unit test: Zoom out генерирует ViewportCommand
- [ ] Unit test: Camera idle сохраняет snapshot
- [ ] Unit test: Center marker menu open/close

- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| ViewportComponent created | PENDING |
| ViewportStore works | PENDING |
| Commands via Output | PENDING |
| Tests pass | PENDING |
