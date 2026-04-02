# Tasklist - MAP-REFACTOR-005

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope

- [x] Create `feature/map/impl/viewport/` package structure
- [x] Create `ViewportComponent` interface with Output
- [x] Create `ViewportModel` data class
- [x] Create `ViewportStore` interface
- [x] Create `ViewportStore.State`
- [x] Create `ViewportStore.Intent`
- [x] Create `ViewportStore.Label` (ViewportCommand)
- [x] Create `ViewportExecutor`
- [x] Create `ViewportReducer`
- [x] Create `ViewportStoreFactory`
- [x] Create `DefaultViewportComponent`

- [x] Unit test: Zoom in генерирует ViewportCommand
- [x] Unit test: Zoom out генерирует ViewportCommand
- [x] Unit test: Camera idle сохраняет snapshot
- [x] Unit test: Center marker menu open/close

- [x] Run `./gradlew :composeApp:compileDebugKotlinAndroid`
- [x] Run `./gradlew :composeApp:compileKotlinJvm`
- [x] Run `./gradlew :composeApp:test`
- [x] Run `ktlintCheck`

- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| ViewportComponent created | DONE |
| ViewportStore works | DONE |
| Commands via Output | DONE |
| Tests pass | DONE |

## Notes
- Doc/code mismatch: план указывает только `feature/map/impl/viewport/`, но для подключения нового компонента также изменен `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/DefaultMapScreenComponent.kt`.
- Legacy viewport branches removed from `MapStore`; zoom command and center marker menu ownership now live in the dedicated viewport component.
- Implementation-detail map tests moved from `composeApp/src/commonTest/kotlin/ru/tech/demomapapp/feature/map/impl/` to `composeApp/src/jvmTest/kotlin/ru/tech/demomapapp/feature/map/impl/` so full `:composeApp:test` no longer breaks Android unit test compilation.
- Suggested commit message: `refactor map viewport into dedicated component`
