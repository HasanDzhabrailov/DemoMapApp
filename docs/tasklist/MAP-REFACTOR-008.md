# Tasklist - MAP-REFACTOR-008

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

## Delete Old UI
- [ ] Delete `feature/map/ui/MapScreenOverlays.kt`

## Create New UI Files
- [ ] Create updated `MapScreenContent.kt`
- [ ] Create `MapRendererHost.kt` (wrapper)
- [ ] Create `DrawingContent.kt` (DrawingComponent only)
- [ ] Create `DrawingOverlays.kt` (sheets, controls)
- [ ] Create `RulerOverlay.kt` (RulerComponent only)
- [ ] Create `LocationControls.kt` (LocationComponent only)
- [ ] Create `ViewportControls.kt` (ViewportComponent only)
- [ ] Create `ZoomControls.kt`
- [ ] Create `CenterMarker.kt`
- [ ] Create `ToolsOverlay.kt` (ToolsComponent only)
- [ ] Create `ToolsMenuOverlay.kt`
- [ ] Create `AvailableMapsSheet.kt`
- [ ] Create `LayerManagementSheets.kt`

## Create Tests
- [ ] Create `DrawingStoreTest.kt` (reducer + executor)
- [ ] Create `RulerStoreTest.kt`
- [ ] Create `LocationStoreTest.kt`
- [ ] Create `ViewportStoreTest.kt`
- [ ] Create `ToolsStoreTest.kt`
- [ ] Create `MapRouterStoreTest.kt` (aggregation)
- [ ] Create `DefaultMapScreenComponentTest.kt` (integration)

## Verification
- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`
- [ ] Run `detekt`

## Manual Testing
- [ ] Create point flow works
- [ ] Create line flow works
- [ ] Create polygon flow works
- [ ] Ruler toggle works
- [ ] Location GPS works
- [ ] Zoom controls work
- [ ] Layer management works
- [ ] All sheets open/close correctly

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| Old UI deleted | PENDING |
| New UI created | PENDING |
| Component UIs isolated | PENDING |
| Tests created | PENDING |
| All tests pass | PENDING |
| Manual testing OK | PENDING |
