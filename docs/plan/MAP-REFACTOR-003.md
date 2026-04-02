# Plan — MAP-REFACTOR-003

## Summary
Изолировать Ruler. Получает Location updates через explicit method от parent (не глобальный EventBus).

## Affected Modules
- `feature/map/impl/ruler/` — новый пакет

## File-Level Plan
- Create `RulerComponent` interface with `onLocationUpdated` method
- Create `RulerModel` with isEnabled, measurement, infoWindow
- Create `RulerStore` with State/Intent/Label
- Executor uses RulerMeasurementCalculator and RulerInfoWindowStateFormatter
- Parent calls `onLocationUpdated` explicitly when location changes

## MVIKotlin Mapping
- Intent:
  - ToggleClicked
  - LocationUpdated(location)
  - CameraSnapshotReceived(snapshot)
- State:
  - `isEnabled: Boolean`
  - `currentLocation: MapLocationMarker?`
  - `lastCameraSnapshot: MapCameraSnapshot?`
  - `measurement: RulerMeasurement?`
  - `infoWindow: RulerInfoWindowState?`
- Reducer:
  - Toggle enabled state
  - Update location and snapshot
  - Clear measurement when disabled
- Executor:
  - Calculate measurement using RulerMeasurementCalculator (side effect)
  - Format info window using RulerInfoWindowStateFormatter (side effect)
- Label:
  - ViewportCommandRequested (to update measurements)

## Migration Strategy
1. Create ruler package
2. Define RulerComponent API
3. Implement RulerStore
4. Wire calculator and formatter in Executor
5. Create DefaultRulerComponent
6. Test explicit location dependency

## Risks and Mitigations
- Risk: Tight coupling to calculator/formatter
  - Mitigation: Inject as dependencies, mock in tests
- Risk: Measurement updates on every location change
  - Mitigation: Debounce in executor if needed

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- Unit test: Toggle enables/disables ruler
- Unit test: Location update triggers measurement
- Unit test: Output sends ViewportCommand

## Task Breakdown
1. Create ruler package
2. Define RulerComponent interface
3. Create RulerStore structure
4. Implement Executor with calculator/formatter
5. Implement Reducer
6. Create Factory and DefaultRulerComponent
7. Write unit tests
8. Verify build and tests
