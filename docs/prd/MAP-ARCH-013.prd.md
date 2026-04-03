# MAP-ARCH-013 — Remove bridge: feature info coordination

## Context
- `MapScreenRouterBridge` currently participates in feature info and selection coordination.

## Goal
Remove feature info coordination responsibility from `MapScreenRouterBridge`.

## Non-Goals
- No removal of viewport or overlay or menu bridge responsibilities.

## User Scenarios
- Feature info flow is owned by clearer screen or host logic.

## Acceptance Criteria
- [ ] Feature info coordination no longer lives in `MapScreenRouterBridge`.
- [ ] Equivalent behavior is preserved.
- [ ] Project compiles and tests pass.

## Constraints
- One bridge responsibility only.

## Success Metrics
- One central bridge concern is removed.

## Open Questions
- Should feature info coordination remain in mapscreen or host?
