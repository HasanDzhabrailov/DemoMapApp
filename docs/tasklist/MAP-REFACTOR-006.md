# Tasklist - MAP-REFACTOR-006

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

- [ ] Create `feature/map/impl/tools/` package structure
- [ ] Create `ToolsComponent` interface with Output
- [ ] Create `ToolsModel` data class
- [ ] Create `ToolsStore` interface
- [ ] Create `ToolsStore.State`
- [ ] Create `ToolsStore.Intent`
- [ ] Create `ToolsStore.Label` (LayersChanged)
- [ ] Create `ToolsExecutor`
- [ ] Create `ToolsReducer`
- [ ] Migrate logic from `MapLayerManagementReducer`
- [ ] Create `ToolsStoreFactory`
- [ ] Create `DefaultToolsComponent`

- [ ] Unit test: Layer move up/down
- [ ] Unit test: Layer remove
- [ ] Unit test: Layer opacity change
- [ ] Unit test: Available maps selection
- [ ] Unit test: Output callback sends LayersChanged

- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| ToolsComponent created | PENDING |
| ToolsStore works | PENDING |
| Layer operations work | PENDING |
| Tests pass | PENDING |
