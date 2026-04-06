# Plan — ARCH-CLEANUP-MAP-003

## Summary
Move viewport command routing ownership out of map host glue.

## Affected Modules
- `feature/map`

## Implementation Notes
- Resolve only viewport command routing and consumption logic in host.
- Keep behavior stable.

## Risks
- Risk 1: Pending viewport command consumption may regress.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Confirm current viewport command routing path in `DefaultMapHostComponent`.
2. Move command ownership out of host glue.
3. Run tests.
