# Plan — MAP-ARCH-005

## Summary
Repackage tools feature classes into a dedicated tools package.

## Affected Modules
- `feature/map/impl`
- `feature/map/tools`
- `feature/map/ui`

## Implementation Notes
- Move only tools-owned classes.
- Keep behavior unchanged.

## Risks
- Risk 1: Ownership of some UI files may be mixed.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify tools-owned files.
2. Move tools files.
3. Fix imports.
4. Compile and test.
