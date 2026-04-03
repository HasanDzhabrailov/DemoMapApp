# Plan — MAP-ARCH-015

## Summary
Delete the remaining bridge and obsolete references after responsibilities have been migrated out.

## Affected Modules
- `feature/map/host`
- `feature/map/mapscreen`

## Implementation Notes
- Remove bridge class.
- Remove wiring and references.

## Risks
- Risk 1: Hidden dependency on bridge may remain.

## Verification
- `./gradlew :composeApp:test`
- `./gradlew :composeApp:compileKotlinJvm`

## Task Breakdown
1. Delete bridge class.
2. Remove bridge references.
3. Compile and test.
