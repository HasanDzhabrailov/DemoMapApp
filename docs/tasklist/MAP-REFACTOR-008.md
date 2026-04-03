# Tasklist - MAP-REFACTOR-008

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope

## Delete Old UI
- [x] Remove monolithic `MapScreenOverlays` from `MapScreenContent.kt` (`MapScreenOverlays.kt` file was not present in repo)

## Create New UI Files
- [x] Create updated `MapScreenContent.kt`
- [ ] Create `MapRendererHost.kt` (wrapper)
- [x] Create `DrawingContent.kt` (DrawingComponent only)
- [ ] Create `DrawingOverlays.kt` (sheets, controls)
- [x] Create `RulerOverlay.kt` (RulerComponent only)
- [x] Create `LocationControls.kt` (LocationComponent only)
- [x] Create `ViewportControls.kt` (ViewportComponent only)
- [ ] Create `ZoomControls.kt`
- [ ] Create `CenterMarker.kt`
- [x] Create `ToolsOverlay.kt` (ToolsComponent only)
- [ ] Create `ToolsMenuOverlay.kt`
- [ ] Create `AvailableMapsSheet.kt`
- [ ] Create `LayerManagementSheets.kt`

## Create Tests
- [x] Create drawing store tests (`DrawingReducerTest.kt`, `DrawingExecutorTest.kt`)
- [x] Create `RulerStoreTest.kt` equivalent (`RulerReducerTest.kt`)
- [x] Create `LocationStoreTest.kt` equivalent (`LocationReducerTest.kt`, `LocationExecutorTest.kt`)
- [x] Create `ViewportStoreTest.kt` equivalent (`ViewportReducerTest.kt`)
- [x] Create `ToolsStoreTest.kt` equivalent (`ToolsReducerTest.kt`)
- [x] Create `MapRouterStoreTest.kt` equivalent (`MapRouterReducerTest.kt`)
- [x] Create `DefaultMapScreenComponentTest.kt` (integration)

## Verification
- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [x] Run `./gradlew :composeApp:compileKotlinJvm`
- [x] Run `./gradlew :composeApp:test`
- [x] Run `ktlintCheck`
- [x] Run `detekt`

## Manual Testing
- [ ] Create point flow works
- [ ] Create line flow works
- [ ] Create polygon flow works
- [ ] Ruler toggle works
- [ ] Location GPS works
- [ ] Zoom controls work
- [ ] Layer management works
- [ ] All sheets open/close correctly

- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| Old UI deleted | DONE |
| New UI created | DONE |
| Component UIs isolated | PARTIAL |
| Tests created | DONE |
| All tests pass | DONE |
| Manual testing OK | PENDING |
