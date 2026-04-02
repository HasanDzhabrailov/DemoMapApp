# Tasklist - MAP-REFACTOR-007

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

## Delete Old Files
- [ ] Delete `feature/map/impl/store/MapStore.kt`
- [ ] Delete `feature/map/impl/store/MapStoreExecutor.kt`
- [ ] Delete `feature/map/impl/store/MapStoreReducer.kt`
- [ ] Delete `feature/map/impl/store/MapStoreMessage.kt`
- [ ] Delete `feature/map/impl/store/MapStoreFactory.kt`
- [ ] Delete `feature/map/impl/store/MapStoreHolder.kt`
- [ ] Delete `feature/map/impl/store/handler/CreatePointHandler.kt`
- [ ] Delete `feature/map/impl/store/handler/DrawingHandler.kt`
- [ ] Delete `feature/map/impl/store/handler/LocationHandler.kt`
- [ ] Delete `feature/map/impl/store/handler/FeatureClickHandler.kt`
- [ ] Delete `feature/map/impl/store/handler/` directory
- [ ] Delete old `feature/map/api/MapScreenComponent.kt`
- [ ] Delete old `feature/map/impl/DefaultMapScreenComponent.kt`
- [ ] Delete `feature/map/ui/MapScreenOverlays.kt`
- [ ] Delete old tests `MapStoreExecutorTest.kt`
- [ ] Delete old tests `MapStoreReducerTest.kt`

## Create New Files
- [ ] Create new `MapScreenComponent.kt` (updated API)
- [ ] Create new `MapScreenModel` data class
- [ ] Create new `DefaultMapScreenComponent.kt` (Router)
- [ ] Wire DrawingComponent with Output callback
- [ ] Wire RulerComponent with Output callback
- [ ] Wire LocationComponent with Output callback
- [ ] Wire ViewportComponent with Output callback
- [ ] Wire ToolsComponent with Output callback
- [ ] Connect child Outputs to RouterStore intents
- [ ] Aggregate model from RouterStore

## Integration
- [ ] Import MAP-REFACTOR-001 (RouterStore)
- [ ] Import MAP-REFACTOR-002 (Drawing)
- [ ] Import MAP-REFACTOR-003 (Ruler)
- [ ] Import MAP-REFACTOR-004 (Location)
- [ ] Import MAP-REFACTOR-005 (Viewport)
- [ ] Import MAP-REFACTOR-006 (Tools)

## Verification
- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`
- [ ] Run `detekt`

## Tests
- [ ] Integration test: Full point creation flow
- [ ] Integration test: Ruler with location updates
- [ ] Integration test: Layer management
- [ ] Integration test: Viewport commands

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| Old files deleted | PENDING |
| Router component created | PENDING |
| All children wired | PENDING |
| Build passes | PENDING |
| Integration tests pass | PENDING |
