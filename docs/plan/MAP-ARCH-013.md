# Plan — MAP-ARCH-013

## Summary
Remove feature info coordination from the bridge and relocate it to clearer ownership.

## Affected Modules
- `feature/map/host`
- `feature/map/mapscreen`

## Implementation Notes
- Migrate one bridge concern only.

## Risks
- Risk 1: Selection dismissal rules may regress.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify feature info coordination paths.
2. Relocate responsibility.
3. Remove bridge branch.
4. Run tests.
