# Tasklist - MAP-REFACTOR-004

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

- [ ] Create `feature/map/impl/location/` package structure
- [ ] Create `LocationComponent` interface with Output
- [ ] Create `LocationModel` data class
- [ ] Create `LocationStore` interface
- [ ] Create `LocationStore.State` с mode/marker/request
- [ ] Create `LocationStore.Intent` (GpsToggle, MyLocation, etc.)
- [ ] Create `LocationStore.Label` (LocationUpdated, ViewportCommand, LocationRequest)
- [ ] Create `LocationExecutor`
- [ ] Create `LocationReducer`
- [ ] Create `LocationStoreFactory`
- [ ] Create `DefaultLocationComponent`
- [ ] Migrate logic from `LocationHandler`

- [ ] Unit test: GPS toggle переключает режимы
- [ ] Unit test: My Location запрашивает permission
- [ ] Unit test: LocationResult обновляет marker
- [ ] Unit test: Output callbacks работают

- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| LocationComponent created | PENDING |
| LocationStore works | PENDING |
| All modes work | PENDING |
| Tests pass | PENDING |
