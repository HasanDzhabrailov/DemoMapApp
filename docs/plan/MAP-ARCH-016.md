# Plan — MAP-ARCH-016

## Summary
Remove impl-cast usage from map UI and depend on explicit contracts.

## Affected Modules
- `feature/map/ui`
- `feature/map/api`
- `feature/map/host`

## Implementation Notes
- Introduce the contract boundary needed by UI.
- Remove the impl cast from `MapScreenContent`.

## Risks
- Risk 1: Contract may become too broad if it is not kept narrow.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Define a UI-facing contract boundary.
2. Update `MapScreenContent`.
3. Update previews and tests.
4. Compile and test.
