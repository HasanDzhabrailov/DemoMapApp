# Tasklist - MAP-REFACTOR-003

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope

- [x] Create `feature/map/impl/ruler/` package structure
- [x] Create `RulerComponent` interface
- [x] Create `RulerModel` data class
- [x] Create `RulerStore` interface
- [x] Create `RulerStore.State` с isEnabled/measurement/infoWindow
- [x] Create `RulerStore.Intent` (Toggle, LocationUpdated, CameraSnapshot)
- [x] Create `RulerStore.Label` (ViewportCommand)
- [x] Create `RulerExecutor` с RulerMeasurementCalculator
- [x] Create `RulerReducer`
- [x] Create `RulerStoreFactory`
- [x] Create `DefaultRulerComponent`
- [x] Wire calculator и formatter как dependencies

- [x] Unit test: Toggle включает/выключает ruler
- [x] Unit test: LocationUpdated обновляет measurement
- [x] Unit test: Output callback отправляет ViewportCommand

- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [x] Run `./gradlew :composeApp:compileKotlinJvm`
- [x] Run `./gradlew :composeApp:test`
- [ ] Run `ktlintCheck`

- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| RulerComponent created | DONE |
| RulerStore works | DONE |
| Explicit Location dependency | DONE |
| Tests pass | DONE |

## Notes
- `ktlintCheck` still fails because of a pre-existing unrelated violation in `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/MapLayerSourceConverter.android.kt:49`.
- Suggested commit message: `extract ruler store into isolated component`
