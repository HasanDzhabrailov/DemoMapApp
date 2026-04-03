# Plan — MAP-ARCH-017

## Summary
Split the oversized screen contract into narrower host and child-oriented contracts.

## Affected Modules
- `feature/map/api`
- `feature/map/host`
- `feature/map/ui`

## Implementation Notes
- Define narrow contracts.
- Update UI and previews.

## Risks
- Risk 1: Contract churn may affect tests and previews.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Define target contracts.
2. Update implementations.
3. Update UI and previews.
4. Run tests.
