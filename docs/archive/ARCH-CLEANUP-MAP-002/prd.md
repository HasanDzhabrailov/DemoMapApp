# ARCH-CLEANUP-MAP-002 — Remove manual ruler input synchronization from map host

## Context
- `DefaultMapHostComponent` still manually mirrors viewport and location updates into the ruler feature via cached fields and `syncRulerInputs`.
- This keeps cross-feature synchronization in the host instead of a bounded parent-owned state flow.

## Goal
Remove manual ruler input synchronization glue from `DefaultMapHostComponent`.

## Non-Goals
- No unrelated map cleanup.
- No redesign of renderer or UI contracts beyond what is required for ruler input ownership.

## User Scenarios
- Ruler still reacts correctly to camera and location changes.
- Map host no longer owns cached ruler sync state.

## Acceptance Criteria
- [ ] `DefaultMapHostComponent` no longer uses cached fields like `syncedRulerLocation` and `syncedRulerSnapshot` for ruler synchronization.
- [ ] Ruler camera/location input flow is owned by a bounded parent/store path instead of manual host mirroring.
- [ ] Project compiles and relevant tests pass.

## Constraints
- Keep scope limited to ruler input synchronization cleanup in map area.
- Preserve existing ruler behavior.

## Success Metrics
- Cross-feature ruler input glue in map host is reduced.

## Open Questions
- Should parent-owned ruler inputs live in the existing router store or in a dedicated bounded parent store?
