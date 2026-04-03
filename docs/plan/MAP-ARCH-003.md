# Plan — MAP-ARCH-003

## Summary
Repackage current map host and orchestration classes into a dedicated host package while preserving existing behavior.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/impl`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/host`

## Implementation Notes
- Identify current host and orchestration classes.
- Move only host-owned files.
- Update imports and package declarations.
- Keep contracts and behavior unchanged.
- Defer responsibility cleanup to later tickets.

## Risks
- Risk 1: Moving mixed-responsibility files may reveal unclear ownership.
- Risk 2: Temporary package placement may still need later refinement.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify host-owned map files.
2. Move host files into the dedicated package.
3. Update imports and references.
4. Compile project.
5. Run relevant tests.
6. Confirm the move is behavior-preserving only.
