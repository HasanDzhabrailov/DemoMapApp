# Plan — MAP-ARCH-014

## Summary
Remove overlay and menu coordination from the bridge and relocate it to clearer ownership.

## Affected Modules
- `feature/map/host`
- `feature/map/tools`
- `feature/map/viewport`
- `feature/map/drawing`

## Implementation Notes
- Migrate one bridge concern only.

## Risks
- Risk 1: Dismiss behavior may regress in edge cases.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify overlay and menu coordination paths.
2. Relocate responsibility.
3. Remove bridge branch.
4. Run tests.
