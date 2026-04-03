# Tasklist - MAP-REFACTOR-007

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope

## Delete Old Files
- [x] Delete `feature/map/impl/store/MapStore.kt`
- [x] Delete `feature/map/impl/store/MapStoreExecutor.kt`
- [x] Delete `feature/map/impl/store/MapStoreReducer.kt`
- [x] Delete `feature/map/impl/store/MapStoreMessage.kt`
- [x] Delete `feature/map/impl/store/MapStoreFactory.kt`
- [x] Delete `feature/map/impl/store/MapStoreHolder.kt`
- [x] Delete `feature/map/impl/store/handler/CreatePointHandler.kt`
- [x] Delete `feature/map/impl/store/handler/DrawingHandler.kt`
- [x] Delete `feature/map/impl/store/handler/LocationHandler.kt`
- [x] Delete `feature/map/impl/store/handler/FeatureClickHandler.kt`
- [x] Delete `feature/map/impl/store/handler/` directory
- [ ] Delete old `feature/map/api/MapScreenComponent.kt`
- [x] Delete old `feature/map/impl/DefaultMapScreenComponent.kt`
- [x] Delete `feature/map/ui/MapScreenOverlays.kt`
- [x] Delete old tests `MapStoreExecutorTest.kt`
- [x] Delete old tests `MapStoreReducerTest.kt`

## Create New Files
- [x] Create new `MapScreenComponent.kt` (updated API)
- [ ] Create new `MapScreenModel` data class
- [x] Create new `DefaultMapScreenComponent.kt` (Router)
- [x] Wire DrawingComponent with Output callback
- [x] Wire RulerComponent with Output callback
- [x] Wire LocationComponent with Output callback
- [x] Wire ViewportComponent with Output callback
- [x] Wire ToolsComponent with Output callback
- [x] Connect child Outputs to RouterStore intents
- [x] Aggregate model from RouterStore

## Integration
- [x] Import MAP-REFACTOR-001 (RouterStore)
- [x] Import MAP-REFACTOR-002 (Drawing)
- [x] Import MAP-REFACTOR-003 (Ruler)
- [x] Import MAP-REFACTOR-004 (Location)
- [x] Import MAP-REFACTOR-005 (Viewport)
- [x] Import MAP-REFACTOR-006 (Tools)

## Verification
- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [x] Run `./gradlew :composeApp:compileKotlinJvm`
- [x] Run `./gradlew :composeApp:test`
- [x] Run `ktlintCheck`
- [x] Run `detekt`

## Tests
- [x] Integration test: Full point creation flow
- [x] Integration test: Ruler with location updates
- [x] Integration test: Layer management
- [x] Integration test: Viewport commands

- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| Old files deleted | DONE |
| Router component created | DONE |
| All children wired | DONE |
| Build passes | DONE |
| Integration tests pass | DONE |

## Notes

- `MapScreenComponent` facade was preserved per PRD non-goal.
- Separate `MapScreenModel` file was not introduced: aggregated model remains `MapScreenComponent.Model`, but aggregation source moved into `MapRouterStore`.
