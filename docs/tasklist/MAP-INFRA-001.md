# Tasklist — MAP-INFRA-001

- [x] Read PRD
- [x] Read plan
- [x] Create :map module structure
- [x] Create build script with MapLibre
- [x] Move MapViewHolder to :map
- [x] Move MapRenderer to :map
- [x] Move lifecycle binding to :map
- [x] Define style access contract via `MapRenderer` callbacks and `MapViewHolder.loadStyle`
- [x] Update feature dependencies
- [x] Verify :map builds
- [x] Verify composeApp builds
- [x] Update this tasklist
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| :map module created | DONE |
| Infrastructure moved | DONE |
| Style access defined | DONE |
| Build successful | DONE |

## Verification Notes
- Added `map/src/jvmTest/kotlin/ru/tech/demomapapp/map/StyleLoadCoordinatorTest.kt`
- `./gradlew.bat :map:jvmTest`
- `./gradlew.bat :map:build`
- `./gradlew.bat :composeApp:build`
- `./gradlew.bat :map:processDebugManifest :composeApp:processDebugMainManifest`
- Post-fix: reordered `viewport`, `location`, and `ruler` component initialization in `DefaultMapHostComponent` to prevent startup NPE during ruler input subscription
- `./gradlew.bat :composeApp:compileDebugKotlinAndroid`
- Post-fix: unified tools child-slot instances to `ToolsUiContract.Child` so `ToolsOverlay` can detect active menu states and render settings dialogs
- `./gradlew.bat :feature:map:impl:jvmTest --tests ru.tech.demomapapp.feature.map.tools.DefaultToolsComponentTest`

## Suggested Commit Message
- `refactor: move base MapLibre infrastructure into map module`
