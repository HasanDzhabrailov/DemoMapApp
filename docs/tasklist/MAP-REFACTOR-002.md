# Tasklist - MAP-REFACTOR-002

## Status: DONE ✅

### Completed Tasks:
- [x] Read PRD
- [x] Read plan
- [x] Confirm scope
- [x] Create `feature/map/impl/drawing/` package structure
- [x] Create `DrawingComponent` interface with Output callback
- [x] Create `DrawingModel` data class (9 fields - meets ~10 requirement)
- [x] Create `DrawingStore` interface (State/Intent/Message/Label)
- [x] Create `DrawingStore.State` with points/lines/polygons/drafts/cameraSnapshot
- [x] Create `DrawingStore.Intent` sealed interface
- [x] Create `DrawingStore.Label` for FeatureCreated
- [x] Create `DrawingStore.Message` sealed interface
- [x] Create `DrawingExecutor` (use case calls, side effects)
- [x] Create `DrawingReducer` (pure, no time/IO)
- [x] Create `DrawingStoreFactory`
- [x] Create `DrawingStoreHolder`
- [x] Create `DefaultDrawingComponent` (wires store + output, no business logic)
- [x] Migrate logic from `DrawingHandler`
- [x] Migrate logic from `CreatePointHandler`
- [x] Fix DrawingPositionAdded to actually add vertices using camera snapshot
- [x] Add CameraPositionUpdated intent for tracking camera position
- [x] Add toVertex() extension for MapCameraSnapshot
- [x] Unit test: CreatePoint flow
- [x] Unit test: CreateLine flow
- [x] Unit test: CreatePolygon flow
- [x] Unit test: Draft updates
- [x] Unit test: Output callback called on FeatureCreated
- [x] Unit test: DrawingPositionAdded with camera snapshot
- [x] Unit test: CameraPositionUpdated
- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid` - PASS
- [x] Run `./gradlew :composeApp:test` - PASS (18 tests)

## PRD Compliance Check ✅

| Requirement | Status | Evidence |
|-------------|--------|----------|
| DrawingComponent interface with Output callback | ✅ | `DrawingComponent.kt:28-30` |
| DrawingModel ~10 fields | ✅ | `DrawingModel.kt:25-34` (9 fields) |
| DrawingStore State/Intent/Label | ✅ | `DrawingStore.kt:10-91` |
| FeatureCreated via Output (not EventBus) | ✅ | `DefaultDrawingComponent.kt:41-54` |
| Use cases in Executor | ✅ | `DrawingExecutor.kt:115-131, 133-170` |
| Reducer pure (no time/IO) | ✅ | `DrawingReducer.kt` - only state transformations |
| Executor handles side effects | ✅ | `DrawingExecutor.kt` - use cases, timeProvider, featureIdProvider |
| No business logic in Component | ✅ | `DefaultDrawingComponent.kt` - only wiring |

## Expected Results
| Check | Status |
|-------|--------|
| DrawingComponent created | ✅ DONE |
| DrawingStore works | ✅ DONE |
| Output callback explicit | ✅ DONE |
| Use cases in Executor | ✅ DONE |
| Tests pass | ✅ DONE (18 tests) |
| Drawing fully isolated | ✅ DONE |
| Type-safe output | ✅ DONE |

## Files Created/Modified:
1. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingComponent.kt`
2. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingModel.kt`
3. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingStore.kt`
4. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingExecutor.kt`
5. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingReducer.kt`
6. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingStoreFactory.kt`
7. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingStoreHolder.kt`
8. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DefaultDrawingComponent.kt`
9. `composeApp/src/commonTest/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingExecutorTest.kt`
10. `composeApp/src/commonTest/kotlin/ru/tech/demomapapp/feature/map/impl/drawing/DrawingReducerTest.kt`

## Suggested Commit Message:
```
feat(MAP-REFACTOR-002): Isolate Drawing feature into separate component

- Create DrawingComponent with explicit Output callback
- Implement DrawingStore with MVIKotlin (State/Intent/Message/Label)
- Move drawing logic from DrawingHandler and CreatePointHandler to Executor
- Create pure DrawingReducer for state transitions
- Add comprehensive unit tests for CreatePoint, CreateLine, CreatePolygon flows
- Fix DrawingPositionAdded to properly add vertices using camera snapshot
- Output callback publishes FeatureCreated events (Point/Line/Polygon)

Breaking change: Drawing logic is now isolated and testable independently
```
