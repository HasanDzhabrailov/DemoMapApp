# Plan — MAP-ARCH-020

## Summary
Review and migrate remaining eligible ephemeral map flows to official Decompose navigation.

## Affected Modules
- `feature/map`
- `feature/map/ui`

## Implementation Notes
- Migrate only flows where navigation semantics are real.
- Leave simple visibility state alone if navigation is not justified.

## Risks
- Risk 1: Overuse of navigation could add complexity.

## Verification
- `./gradlew :composeApp:test`
- `./gradlew :composeApp:compileDebugKotlinAndroid`

## Task Breakdown
1. Inventory remaining ephemeral flows.
2. Select navigation-eligible flows.
3. Migrate selected flows.
4. Run tests and compile.
