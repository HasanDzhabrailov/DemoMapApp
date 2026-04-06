# Plan — ARCH-CLEANUP-ROOT-001

## Summary
Apply small, audit-driven cleanup changes in the root area.

## Affected Modules
- `root`

## Implementation Notes
- Resolve only selected audit items.

## Risks
- Risk 1: Cleanup may accidentally expand into redesign.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Select cleanup subset.
2. Apply cleanup.
3. Run tests.
