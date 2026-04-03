# Plan — MAP-ARCH-008

## Summary
Repackage ruler feature classes into a dedicated ruler package.

## Affected Modules
- `feature/map/impl`
- `feature/map/ruler`
- `feature/map/ui`

## Implementation Notes
- Move only ruler-owned classes.
- Keep behavior unchanged.

## Risks
- Risk 1: Some ruler UI files may have mixed ownership.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify ruler-owned files.
2. Move ruler files.
3. Fix imports.
4. Compile and test.
