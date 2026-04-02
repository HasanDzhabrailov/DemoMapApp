# Tasklist - MAP-REFACTOR-004

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope

- [x] Create `feature/map/impl/location/` package structure
- [x] Create `LocationComponent` interface with Output
- [x] Create `LocationModel` data class
- [x] Create `LocationStore` interface
- [x] Create `LocationStore.State` с mode/marker/request
- [x] Create `LocationStore.Intent` (GpsToggle, MyLocation, etc.)
- [x] Create `LocationStore.Label` (LocationUpdated, ViewportCommand, LocationRequest)
- [x] Create `LocationExecutor`
- [x] Create `LocationReducer`
- [x] Create `LocationStoreFactory`
- [x] Create `DefaultLocationComponent`
- [x] Migrate logic from `LocationHandler`

- [x] Unit test: GPS toggle переключает режимы
- [x] Unit test: My Location behavior covered (docs mismatch: repo keeps manual placeholder behavior)
- [x] Unit test: LocationResult обновляет marker
- [x] Unit test: Output callbacks работают

- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [x] Run `./gradlew :composeApp:compileKotlinJvm`
- [x] Run `./gradlew :composeApp:test`
- [x] Run `ktlintCheck`

- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| LocationComponent created | DONE |
| LocationStore works | DONE |
| All modes work | DONE |
| Tests pass | DONE |
