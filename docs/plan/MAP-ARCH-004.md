# Plan — MAP-ARCH-004

## Summary
Repackage renderer-facing map screen code into a dedicated mapscreen package while preserving current behavior.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/mapscreen`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/ui`

## Implementation Notes
- Identify map-screen-owned classes and mappers.
- Move only files owned by the map-screen responsibility.
- Update imports and package declarations.
- Keep current public behavior unchanged.
- Defer responsibility cleanup to later tickets.

## Risks
- Risk 1: Some classes may still have mixed screen and host ownership.
- Risk 2: Package move may expose missing contract boundaries.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify map-screen-owned files.
2. Move map-screen-owned files into the dedicated package.
3. Update imports and references.
4. Compile project.
5. Run relevant tests.
6. Confirm no behavior changes were introduced.
