# Plan — ARCH-CLEANUP-MAP-004

## Summary
Reduce overlay exclusivity orchestration in the map host.

## Affected Modules
- `feature/map`

## Implementation Notes
- Resolve only overlay dismissal and exclusivity glue in host.
- Keep change minimal and behavior-preserving.

## Risks
- Risk 1: Conflicting overlays may remain visible if exclusivity rules are moved incorrectly.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Confirm current overlay exclusivity rules in `DefaultMapHostComponent`.
2. Move ownership of those rules out of host click-handler glue.
3. Run tests.
