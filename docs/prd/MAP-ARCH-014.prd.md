# MAP-ARCH-014 — Remove bridge: overlay/menu coordination

## Context
- `MapScreenRouterBridge` currently coordinates dismissals and overlay or menu interactions.

## Goal
Remove overlay and menu coordination responsibility from `MapScreenRouterBridge`.

## Non-Goals
- No removal of other bridge responsibilities.

## User Scenarios
- Overlay and menu interactions are owned by clearer feature or host boundaries.

## Acceptance Criteria
- [ ] Overlay or menu coordination no longer lives in `MapScreenRouterBridge`.
- [ ] Equivalent behavior is preserved.
- [ ] Project compiles and tests pass.

## Constraints
- One bridge responsibility only.

## Success Metrics
- One central bridge concern is removed.

## Open Questions
- Which dismiss rules belong to host versus feature-level ownership?
