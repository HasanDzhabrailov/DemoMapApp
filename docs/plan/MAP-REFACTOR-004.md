# Plan — MAP-REFACTOR-004

## Summary
Изолировать Location/GPS management. Выносим логику из `LocationHandler`.

## Affected Modules
- `feature/map/impl/location/` — новый пакет
- Удаляем после миграции: `LocationHandler.kt`

## File-Level Plan
- Create `LocationComponent` interface with Output callback
- Create `LocationModel` with mode, currentMarker, pendingRequest
- Create `LocationStore` with State/Intent/Label
- Executor handles location requests and permission logic
- Reducer manages mode transitions

## MVIKotlin Mapping
- Intent:
  - GpsToggled
  - MyLocationClicked
  - CurrentLocationFocusClicked
  - LocationResultReceived(result)
  - LocationRequestConsumed
- State:
  - `mode: MyLocationMode` (OFF, MANUAL_PLACEHOLDER, GPS)
  - `currentMarker: MapLocationMarker?`
  - `pendingRequest: MapLocationRequest?`
  - `hasRealLocation: Boolean`
- Reducer:
  - Mode transitions (OFF → MANUAL → GPS)
  - Update marker on location result
  - Clear pending request
- Executor:
  - Handle location requests (side effect)
  - Permission checks (side effect)
- Label:
  - LocationUpdated (to notify other components)
  - ViewportCommandRequested
  - LocationRequestIssued

## Migration Strategy
1. Create location package
2. Define LocationComponent API
3. Implement LocationStore
4. Migrate LocationHandler logic to Executor
5. Implement Reducer with mode transitions
6. Create Factory and DefaultLocationComponent
7. Write tests

## Risks and Mitigations
- Risk: Complex mode transition logic
  - Mitigation: Explicit state machine in reducer
- Risk: Permission handling leak
  - Mitigation: Keep in executor, not UI

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- Unit test: GPS toggle switches modes
- Unit test: My Location requests permission
- Unit test: LocationResult updates marker

## Task Breakdown
1. Create location package
2. Define LocationComponent interface
3. Create LocationStore structure
4. Migrate LocationHandler to Executor
5. Implement Reducer
6. Create Factory and DefaultLocationComponent
7. Write unit tests
8. Verify build and tests
