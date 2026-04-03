# Plan — MAP-ARCH-009

## Summary
Repackage viewport feature classes into a dedicated viewport package.

## Affected Modules
- `feature/map/impl`
- `feature/map/viewport`
- `feature/map/ui`

## Implementation Notes
- Move only viewport-owned classes.
- Keep behavior unchanged.

## Risks
- Risk 1: Some viewport UI files may have mixed ownership.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify viewport-owned files.
2. Move viewport files.
3. Fix imports.
4. Compile and test.
