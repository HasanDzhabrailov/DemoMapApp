# Plan — ARCH-CLEANUP-OTHER-001

## Summary
Apply small, audit-driven cleanup changes in selected non-map areas.

## Affected Modules
- `selected business areas from audit`

## Implementation Notes
- Resolve only selected audit items.

## Risks
- Risk 1: Cleanup may accidentally become too broad.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Select cleanup subset.
2. Apply cleanup.
3. Run tests.
