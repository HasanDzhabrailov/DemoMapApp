# Plan — MAP-ARCH-002

## Summary
Produce a target package map and dependency policy for `feature/map` before any physical code moves.

## Scope Confirmation
- This ticket is design-only.
- No file moves.
- No behavior changes.

## Repo Reality
- Current code is still centered around `feature/map/impl` with existing supporting packages `feature/map/api`, `feature/map/ui`, `feature/map/render`, and Android-specific `feature/map/location` under `androidMain`.
- The target map below is a future ownership layout for incremental migration. It does not match the current package tree yet.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map`

## Target Package Map

### Top-level ownership areas
- `feature/map/api`
  Stable contracts and neutral map models shared across map areas or external consumers.
- `feature/map/host`
  Parent ownership boundary for child creation, cross-area coordination, and parent-level orchestration.
- `feature/map/mapscreen`
  Map-surface ownership boundary for renderer-facing screen logic and map-specific behavior.
- `feature/map/tools`
  Tools menu, map style selection, overlay layer management, and related state.
- `feature/map/drawing`
  Point/line/polygon creation flows, drawing state, and creation use cases.
- `feature/map/location`
  My-location flows, permission/request handling, and location-focused state. Android adapters stay in the same ownership area under `androidMain`.
- `feature/map/ruler`
  Ruler enablement, measurement, ruler info window, and ruler-specific calculations.
- `feature/map/viewport`
  Camera-related commands, zoom actions, center-marker flow, and viewport-local state.

### Supporting non-owner areas
- `feature/map/ui`
  Compose rendering only. No business ownership. UI files should stay here only when they are thin state-to-view projections or shared presentation wrappers.
- `feature/map/render`
  Renderer contracts in shared code and MapLibre adapters in `androidMain`. No feature business logic.

## Ownership Notes
- Use `feature/map/mapscreen`, not `feature/map/mapscreen/impl`, as the initial target package. The goal of this ticket is a clear ownership boundary, and extra `impl` nesting would add depth before a second responsibility inside `mapscreen` exists.
- `host` is the only map package allowed to coordinate across `mapscreen`, `tools`, `drawing`, `location`, `ruler`, and `viewport`.
- `mapscreen` owns map-surface concerns such as render-model mapping and feature-click interpretation, but not parent orchestration of sibling features.
- `tools`, `drawing`, `location`, `ruler`, and `viewport` each own their own component/store/reducer/executor flow and feature-local models.
- Existing router/bridge orchestration in `feature/map/impl` is target-owned by `host` unless a later ticket moves a map-surface concern into `mapscreen`.

## Current Responsibility Inventory To Target Areas
- Host-owned target: `DefaultMapScreenComponent`, `MapScreenRouterBridge`, `MapScreenRouterStateMappers`, `impl/router/*`, and `MapScreenUiComponent`.
- Mapscreen-owned target: `MapRenderModelMapper`, `MapFeatureSelectionResolver`, `MapFeatureInfoWindowStateMapper`, and other map-surface-only mappers/resolvers.
- Tools-owned target: `impl/tools/*` and tools-specific UI overlays.
- Drawing-owned target: `impl/drawing/*`, `CreateMapPointUseCase`, `CreateMapLineUseCase`, `CreateMapPolygonUseCase`, `MapPointId`, and `PlatformCurrentTimeMillis`.
- Location-owned target: `impl/location/*`, location request/effect UI bindings, and Android location adapters.
- Ruler-owned target: `impl/ruler/*`, `RulerMeasurementCalculator`, `RulerArrowGeometryCalculator`, and ruler-specific formatting.
- Viewport-owned target: `impl/viewport/*` and center-marker / zoom UI overlays.

## Dependency Direction
- `api` depends on no feature implementation package.
- `render` may depend on `api` and its own renderer models only.
- `tools`, `drawing`, `location`, `ruler`, and `viewport` may depend on `api` and their own package internals only.
- `mapscreen` may depend on `api` and `render`.
- `mapscreen` must not depend on `host`.
- `tools`, `drawing`, `location`, `ruler`, and `viewport` must not depend on each other directly.
- Cross-feature coordination between `tools`, `drawing`, `location`, `ruler`, and `viewport` must go through `host`.
- `host` may depend on `api`, `mapscreen`, `tools`, `drawing`, `location`, `ruler`, and `viewport`.
- `ui` may depend on `api` plus narrow contracts from `host`, `mapscreen`, `tools`, `drawing`, `location`, `ruler`, `viewport`, and `render`.
- No business package may depend on `ui`.

## Shared Code Rules
- Put code in `api` only when it is a stable cross-area contract or neutral model that is needed outside one ownership area.
- Put code in `render` only when it is a renderer contract, render model, or platform renderer adapter.
- Keep business calculations in the owning feature package unless two ownership areas already need the same rule through a stable boundary.
- Do not introduce generic dump packages such as `shared`, `common`, `util`, or new bridge-style glue packages inside `feature/map`.
- If code is only reused temporarily because the current structure is still mixed, keep it in the current owner and split it in a later ticket instead of declaring it shared too early.

## Multi-module Alignment
- The target package map is intentionally shallow so each ownership area can later become its own module without unrelated code moves.
- The intended future extraction direction is: `map-api`, `map-host`, `map-mapscreen`, `map-tools`, `map-drawing`, `map-location`, `map-ruler`, and `map-viewport`, with renderer adapters remaining platform-specific.
- The dependency graph is hub-and-spoke by design: `api` is the neutral boundary, `host` is the composition root, and sibling feature areas do not depend on each other.

## Risks
- Risk 1: Overdesign before actual migration.
- Risk 2: Too much nesting reduces clarity instead of improving it.

## Verification
- Review target package map.
- Confirm each current responsibility has one target destination.
- Confirm package dependency directions are acyclic by design.

## Task Breakdown
1. Inventory current map responsibilities.
2. Assign each responsibility to a target package area.
3. Define allowed package dependencies.
4. Define rules for shared map code.
5. Review target structure for modularization readiness.
6. Finalize package map for follow-up tickets.
