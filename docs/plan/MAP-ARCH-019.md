# Plan — MAP-ARCH-019

## Summary
Migrate center marker flow to Decompose navigation without touching unrelated flows.

## Affected Modules
- `feature/map/viewport`
- `feature/map/ui`

## Implementation Notes
- Use one navigation model for the center marker flow.

## Risks
- Risk 1: Menu behavior may regress around dismiss handling.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Model center marker flow as navigation.
2. Wire component and UI.
3. Run tests.
