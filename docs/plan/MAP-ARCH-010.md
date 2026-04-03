# Plan — MAP-ARCH-010

## Summary
Introduce a map host component that owns immediate child components using proper child contexts and preserves current feature behavior.

## Affected Modules
- `feature/map/host`
- `feature/map/mapscreen`
- `root/impl`

## Implementation Notes
- Create a dedicated host component.
- Move immediate child creation into host.
- Use `childContext(key)` for permanent children.
- Keep existing behavior paths working.

## Risks
- Risk 1: Incorrect child ownership migration may break retained instances.
- Risk 2: Temporary duplication of orchestration may be needed during migration.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- `./gradlew :composeApp:compileDebugKotlinAndroid`

## Task Breakdown
1. Create host component contract and implementation.
2. Move child construction to host.
3. Use separate child contexts.
4. Wire root to host.
5. Compile and test.
