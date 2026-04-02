# Tasklist - MAP-REFACTOR-003

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope

- [ ] Create `feature/map/impl/ruler/` package structure
- [ ] Create `RulerComponent` interface
- [ ] Create `RulerModel` data class
- [ ] Create `RulerStore` interface
- [ ] Create `RulerStore.State` с isEnabled/measurement/infoWindow
- [ ] Create `RulerStore.Intent` (Toggle, LocationUpdated, CameraSnapshot)
- [ ] Create `RulerStore.Label` (ViewportCommand)
- [ ] Create `RulerExecutor` с RulerMeasurementCalculator
- [ ] Create `RulerReducer`
- [ ] Create `RulerStoreFactory`
- [ ] Create `DefaultRulerComponent`
- [ ] Wire calculator и formatter как dependencies

- [ ] Unit test: Toggle включает/выключает ruler
- [ ] Unit test: LocationUpdated обновляет measurement
- [ ] Unit test: Output callback отправляет ViewportCommand

- [ ] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [ ] Run `./gradlew :composeApp:compileKotlinJvm`
- [ ] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`

- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| RulerComponent created | PENDING |
| RulerStore works | PENDING |
| Explicit Location dependency | PENDING |
| Tests pass | PENDING |
