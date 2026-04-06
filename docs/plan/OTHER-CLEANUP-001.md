# Plan — OTHER-CLEANUP-001

## Summary
Search for usages of template files and delete if unused.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/Greeting.kt` (conditional delete)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/Platform.kt` (conditional delete)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/Platform.android.kt` (conditional delete)
- `composeApp/src/jvmMain/kotlin/ru/tech/demomapapp/Platform.jvm.kt` (conditional delete)

## Implementation Notes
1. Search codebase for references to `Greeting`, `Platform`, `getPlatform`
2. If no production/test references found, delete files
3. If references exist, document them and abort deletion

## Risks
- Risk 1: Hidden references in tests or preview code
- Risk 2: Potential future use for platform detection

## Verification
- `./gradlew :composeApp:compileDebugKotlin`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Search for Greeting usages
2. Search for Platform/getPlatform usages
3. Delete files if unused
4. Verify compilation
5. Run tests
