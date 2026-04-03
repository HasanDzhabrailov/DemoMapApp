# ARCH-AUDIT-001 Audit Inventory

## Scope
- Repository-wide audit only.
- No production behavior changes.
- Focus: helper classes, glue/orchestration classes, and package-boundary violations.

## Audit Categories
- Glue: classes that mainly coordinate multiple features/components/stores instead of owning one bounded responsibility.
- Helper: trivial wrappers or indirection with weak ownership and low standalone value.
- Package boundary: API or package structure exposing internal implementation details or mixing unrelated ownership.

## Findings By Business Area

### Map

#### High

1. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host/DefaultMapHostComponent.kt`
   Type: Glue
   Evidence:
   - Wires tools, drawing, ruler, viewport, location, and screen components in one parent (`lines 54-138`).
   - Forwards a large host API surface directly to children (`lines 152-302`).
   - Manually synchronizes child state through multiple bridge methods like `syncToolsState`, `syncDrawingState`, `syncLocationState`, `syncRulerState`, `syncViewportState`, `syncCenterMarkerState`, and `syncRulerInputs` (`lines 311-365`).
   Why it is a concern:
   - This is a central orchestration/glue class with multiple reasons to change.
   - It weakens the intended feature ownership boundaries and keeps screen composition dependent on manual state mirroring.
   Cleanup candidate:
   - `ARCH-CLEANUP-MAP-001: split map host glue into a bounded parent component/store`

2. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterStore.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterReducer.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterExecutor.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/mapscreen/DefaultMapScreenComponent.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/mapscreen/MapScreenRouterStateMappers.kt`
   Type: Glue + package split smell
   Evidence:
   - Router store mirrors child state for tools, location, ruler, center marker, and drawing in one aggregate state (`MapRouterStore.kt`, `lines 43-149`).
   - Router model reconstructs `MapScreenComponent.Model` from mirrored child state (`MapRouterStore.kt`, `lines 124-148`).
   - Mapper file converts both child models to router state and router-facing model back to child models (`MapScreenRouterStateMappers.kt`, `lines 16-137`).
   - `DefaultMapScreenComponent` exists mainly as a thin bridge over this aggregate router store (`lines 23-83`).
   Why it is a concern:
   - Child state is duplicated into a second store and translated back and forth.
   - This is evidence of bridge architecture rather than a clean parent-owned feature boundary.
   Cleanup candidate:
   - `ARCH-CLEANUP-MAP-002: collapse duplicated map router state and remove reverse mappers`

3. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/MapScreenUiContract.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/MapScreenHostContracts.kt`
   Type: Package boundary
   Evidence:
   - `feature.map.api` imports `DrawingComponent`, `LocationComponent`, `RulerComponent`, and `ToolsComponent` from internal child areas (`MapScreenUiContract.kt`, `lines 3-12`).
   - Public-facing contract is widened across tools, viewport, drawing, location, and feature interactions (`MapScreenUiContract.kt`, `lines 15-23`; `MapScreenHostContracts.kt`, `lines 4-70`).
   Why it is a concern:
   - The `api` package already knows internal child structure, which weakens future module extraction and stable API boundaries.
   Cleanup candidate:
   - `ARCH-CLEANUP-MAP-003: narrow map API to stable screen contract and hide child components`

#### Medium

4. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/*`
   Type: Package boundary
   Evidence:
   - Map feature root is split across `api`, `drawing`, `host`, `impl`, `location`, `mapscreen`, `render`, `ruler`, `tools`, `ui`, and `viewport`.
   Why it is a concern:
   - Ownership is understandable locally, but the feature-level structure is not a clear `api/impl` split and still includes bridge-oriented packages like `host` and `mapscreen` next to bounded subfeatures.
   - This will make future extraction harder if map is split into modules by ownership.
   Cleanup candidate:
   - `ARCH-CLEANUP-MAP-004: normalize map package ownership under bounded feature areas`

#### Low

5. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/ui/MapToolsMenuOverlay.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/tools/ui/MapToolsMenuOverlay.kt`
   Type: Ownership blur
   Evidence:
   - Two similarly named UI files exist in different ownership areas.
   - `feature.map.ui.MapToolsMenuOverlay.kt` actually contains `MapLeftControlsOverlay`, `MapToolsButton`, `MyLocationButton`, and `CurrentLocationFocusButton` (`lines 23-123`), while `feature.map.tools.ui.MapToolsMenuOverlay.kt` owns the actual tools menu overlay (`lines 28-140`).
   Why it is a concern:
   - Naming and placement make tools-related ownership less obvious than it should be.
   Cleanup candidate:
   - `ARCH-CLEANUP-MAP-005: align tools-related UI naming and ownership`

### Root Shell

#### Low

6. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/root/impl/RootComponentFactory.kt`
   Type: Helper
   Evidence:
   - Single function that only returns `DefaultRootComponent(componentContext)` (`lines 1-7`).
   Why it is a concern:
   - Adds indirection without policy, configuration, or lifecycle ownership.
   Cleanup candidate:
   - `ARCH-CLEANUP-ROOT-001: remove trivial root component factory helper`

### Shared / Other

#### Low

7. `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/Greeting.kt`
   `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/Platform.kt`
   Type: Package boundary noise
   Evidence:
   - Both files remain in the root package outside `app`, `root`, or a feature package.
   - They look like template scaffolding rather than part of current business ownership.
   Why it is a concern:
   - Not a production architecture risk, but it keeps extra non-owned code in the shared root package.
   Cleanup candidate:
   - `ARCH-CLEANUP-OTHER-001: remove leftover KMP template scaffolding from root package`

## Areas That Should Likely Stay As-Is

### Map Subfeatures
- `feature/map/drawing`
- `feature/map/location`
- `feature/map/ruler`
- `feature/map/tools`
- `feature/map/viewport`

Why:
- Each area mostly has a bounded component/store/reducer/executor/ui structure.
- The main issues are above the subfeature level, in the host/router/API layers.

### Root Navigation
- `root/api/RootComponent.kt`
- `root/impl/DefaultRootComponent.kt`
- `root/ui/RootContent.kt`

Why:
- Root shell is thin and has a single clear responsibility.

### Map Rendering
- `feature/map/render` in `commonMain`
- `feature/map/render` in `androidMain`

Why:
- Rendering code appears intentionally separated from business logic and keeps Android-specific map rendering in platform code.

## Suggested Follow-Up Ticket Order
1. `ARCH-CLEANUP-MAP-001` - split map host glue into a bounded parent component/store
2. `ARCH-CLEANUP-MAP-002` - collapse duplicated map router state and remove reverse mappers
3. `ARCH-CLEANUP-MAP-003` - narrow map API to stable screen contract and hide child components
4. `ARCH-CLEANUP-MAP-005` - align tools-related UI naming and ownership
5. `ARCH-CLEANUP-ROOT-001` - remove trivial root component factory helper
6. `ARCH-CLEANUP-OTHER-001` - remove leftover KMP template scaffolding from root package

## Notes
- No code cleanup was performed in this ticket.
- Findings are intended to drive small follow-up cleanup tickets backed by repository evidence.
