# Plan — ROOT-CLEANUP-001

## Summary
Remove `RootComponentFactory.kt` and inline its logic into Android and JVM entry points.

## Affected Modules
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/MainActivity.kt`
- `composeApp/src/jvmMain/kotlin/ru/tech/demomapapp/Main.kt`
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/root/impl/RootComponentFactory.kt` (delete)

## Implementation Notes
- Replace `createRootComponent(componentContext)` with `DefaultRootComponent(componentContext)`
- Ensure `DefaultRootComponent` is accessible from platform source sets
- Update imports in entry point files

## Risks
- Risk 1: Constructor visibility issues across source sets

## Verification
- `./gradlew :composeApp:compileDebugKotlin`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Update MainActivity.kt to use DefaultRootComponent directly
2. Update Main.kt to use DefaultRootComponent directly
3. Delete RootComponentFactory.kt
4. Verify DefaultRootComponent visibility
5. Run compilation and tests
