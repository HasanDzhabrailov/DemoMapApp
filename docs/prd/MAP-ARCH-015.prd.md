# MAP-ARCH-015 — Remove remaining MapScreenRouterBridge

## Context
- Bridge responsibilities were removed in prior tickets and remaining bridge code should be deleted.

## Goal
Delete remaining `MapScreenRouterBridge` and its obsolete wiring.

## Non-Goals
- No unrelated cleanup.

## User Scenarios
- Map architecture no longer depends on central bridge glue.

## Acceptance Criteria
- [ ] `MapScreenRouterBridge` is removed.
- [ ] Obsolete wiring and references are removed.
- [ ] Project compiles and tests pass.

## Constraints
- Delete only after prior bridge concerns are migrated.

## Success Metrics
- Central bridge class no longer exists.

## Open Questions
- Are any temporary compatibility adapters still needed?
