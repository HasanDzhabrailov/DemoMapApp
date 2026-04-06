# Plan — MAP-HOST-003

## Summary
Move business rules from DefaultMapHostComponent to store/executor.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host/DefaultMapHostComponent.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterStore.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl/router/MapRouterExecutor.kt`

## Implementation Notes
1. Identify business rules in host methods (conditional gating)
2. Create intents that include relevant state
3. Store/executor decides action based on state
4. Component becomes thin delegation layer
5. UI can show disabled state based on model

## Risks
- Risk 1: UI feedback for blocked actions needs design
- Risk 2: Multiple business rules may interact

## Verification
- Test center marker during drawing mode
- Test center marker when not drawing
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify all business rules in host
2. Design state representation for UI feedback
3. Move rules to store/executor
4. Update UI for disabled states
5. Test behavior preserved
