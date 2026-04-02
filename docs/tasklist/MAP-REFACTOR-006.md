# Tasklist - MAP-REFACTOR-006

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope

- [x] Create `feature/map/impl/tools/` package structure
- [x] Create `ToolsComponent` interface with Output
- [x] Create `ToolsModel` data class
- [x] Create `ToolsStore` interface
- [x] Create `ToolsStore.State`
- [x] Create `ToolsStore.Intent`
- [x] Create `ToolsStore.Label` (LayersChanged)
- [x] Create `ToolsExecutor`
- [x] Create `ToolsReducer`
- [x] Migrate logic from `MapLayerManagementReducer`
- [x] Create `ToolsStoreFactory`
- [x] Create `DefaultToolsComponent`

- [x] Unit test: Layer move up/down
- [x] Unit test: Layer remove
- [x] Unit test: Layer opacity change
- [x] Unit test: Available maps selection
- [x] Unit test: Output callback sends LayersChanged

- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [x] Run `./gradlew :composeApp:compileKotlinJvm`
- [x] Run `./gradlew :composeApp:test`
- [x] Run `ktlintCheck`

- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| ToolsComponent created | DONE |
| ToolsStore works | DONE |
| Layer operations work | DONE |
| Tests pass | DONE |
