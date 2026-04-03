# Plan — MAP-ARCH-006

## Summary
Repackage drawing feature classes into a dedicated drawing package.

## Affected Modules
- `feature/map/impl`
- `feature/map/drawing`
- `feature/map/ui`

## Implementation Notes
- Move only drawing-owned classes.
- Keep behavior unchanged.

## Risks
- Risk 1: Some drawing UI files may have mixed ownership.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify drawing-owned files.
2. Move drawing files.
3. Fix imports.
4. Compile and test.
