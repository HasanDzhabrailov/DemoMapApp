# Plan — MODULES-001

## Summary
Create Gradle module structure for map feature with api/impl separation.

## Affected Modules
- `settings.gradle.kts` (new includes)
- New: `:feature:map:api`
- New: `:feature:map:impl`
- `composeApp` (dependency updates)

## Implementation Notes
1. Create directory structure for new modules
2. Add module includes to settings.gradle.kts
3. Create build.gradle.kts for each module with proper dependencies
4. Move api contracts to :feature:map:api
5. Move implementations to :feature:map:impl
6. Update composeApp dependencies

## Risks
- Risk 1: KMP source set configuration complexity
- Risk 2: Dependency cycles

## Verification
- `./gradlew :feature:map:api:build`
- `./gradlew :feature:map:impl:build`
- `./gradlew :composeApp:build`

## Task Breakdown
1. Create module directory structure
2. Create build scripts for api module
3. Create build scripts for impl module
4. Move api code to api module
5. Move impl code to impl module
6. Update composeApp dependencies
7. Verify build
