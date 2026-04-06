# Plan — MAP-HOST-001

## Summary
Move overlay coordination logic from DefaultMapHostComponent into store/executor layer.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host/DefaultMapHostComponent.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterStore.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterExecutor.kt`

## Implementation Notes
1. Identify overlay coordination methods in host (routeOverlayInteraction, dismiss methods)
2. Create intents in MapRouterStore for overlay management
3. Move exclusivity logic to executor
4. Host delegates to store via intents instead of manual coordination
5. Store tracks overlay state and emits labels for dismissals

## Risks
- Risk 1: Timing changes in overlay dismissal
- Risk 2: Complexity in router store grows

## Verification
- Manual testing of overlay interactions
- `./gradlew :composeApp:test`
- `./gradlew :composeApp:jvmTest`

## Task Breakdown
1. Identify all overlay coordination in host
2. Add overlay intents to MapRouterStore
3. Implement overlay logic in executor
4. Refactor host to delegate to store
5. Test overlay interactions
