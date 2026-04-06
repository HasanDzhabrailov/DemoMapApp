# Plan — MAP-HOST-002

## Summary
Move viewport command routing from host to store/executor layer.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host/DefaultMapHostComponent.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterStore.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterExecutor.kt`

## Implementation Notes
1. Identify command routing in host (onViewportCommandRequested methods)
2. Create intents for command registration and consumption
3. Move source tracking to store state
4. Executor handles command routing logic
5. Host delegates to store, doesn't track sources manually

## Risks
- Risk 1: Command timing/ordering changes
- Risk 2: Multiple command sources collision

## Verification
- Test viewport commands from viewport, location, ruler
- `./gradlew :composeApp:test`
- `./gradlew :composeApp:jvmTest`

## Task Breakdown
1. Identify command routing in host
2. Add command intents to store
3. Implement command routing in executor
4. Refactor host to delegate
5. Test command flows
