# Plan — MAP-ARCH-007

## Summary
Repackage location feature classes into a dedicated location package.

## Affected Modules
- `feature/map/impl`
- `feature/map/location`
- `feature/map/ui`

## Implementation Notes
- Move only location-owned classes.
- Keep behavior unchanged.

## Risks
- Risk 1: Some location UI files may have mixed ownership.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify location-owned files.
2. Move location files.
3. Fix imports.
4. Compile and test.
