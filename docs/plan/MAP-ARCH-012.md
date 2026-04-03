# Plan — MAP-ARCH-012

## Summary
Remove viewport command routing from the bridge and relocate it to clearer ownership.

## Affected Modules
- `feature/map/host`
- `feature/map/viewport`
- `feature/map/location`
- `feature/map/ruler`

## Implementation Notes
- Migrate one bridge concern only.

## Risks
- Risk 1: Command precedence may regress.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify viewport coordination paths.
2. Relocate responsibility.
3. Remove bridge branch.
4. Run tests.
