# Plan — MAP-REFACTOR-005

## Summary
Изолировать Viewport (камера, zoom). Управление через Store.

## Affected Modules
- `feature/map/impl/viewport/` — новый пакет

## File-Level Plan
- Create `ViewportComponent` interface with Output callback
- Create `ViewportModel` with cameraSnapshot, pendingCommand, menu state
- Create `ViewportStore` with State/Intent/Label
- Reducer handles zoom and camera updates
- Labels send ViewportCommand to parent

## MVIKotlin Mapping
- Intent:
  - CameraIdle(snapshot)
  - ZoomInClicked
  - ZoomOutClicked
  - CenterMarkerClicked
  - CenterMarkerMenuDismissed
  - ViewportCommandConsumed
- State:
  - `cameraSnapshot: MapCameraSnapshot?`
  - `pendingCommand: MapViewportCommand?`
  - `isCenterMarkerMenuVisible: Boolean`
- Reducer:
  - Save camera snapshot
  - Set pending command on zoom
  - Clear command when consumed
  - Toggle menu visibility
- Executor:
  - Minimal (commands go through Label)
- Label:
  - ViewportCommand(command)

## Migration Strategy
1. Create viewport package
2. Define ViewportComponent API
3. Implement ViewportStore
4. Implement Reducer
5. Create Factory and DefaultViewportComponent
6. Write tests

## Risks and Mitigations
- Risk: Command lifecycle management
  - Mitigation: Clear command in reducer after consumption

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- Unit test: Zoom in/out generates ViewportCommand
- Unit test: Camera idle saves snapshot

## Task Breakdown
1. Create viewport package
2. Define ViewportComponent interface
3. Create ViewportStore structure
4. Implement Reducer
5. Create Factory and DefaultViewportComponent
6. Write unit tests
7. Verify build and tests
