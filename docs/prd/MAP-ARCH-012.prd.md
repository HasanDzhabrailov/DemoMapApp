# MAP-ARCH-012 — Remove bridge: viewport command coordination

## Context
- `MapScreenRouterBridge` currently centralizes viewport command routing across features.

## Goal
Remove viewport command coordination responsibility from `MapScreenRouterBridge`.

## Non-Goals
- No removal of other bridge responsibilities.

## User Scenarios
- Viewport command routing is owned by clearer contracts or host orchestration.

## Acceptance Criteria
- [ ] Viewport command coordination no longer lives in `MapScreenRouterBridge`.
- [ ] Equivalent behavior is preserved.
- [ ] Project compiles and tests pass.

## Constraints
- One bridge responsibility only.

## Success Metrics
- One central bridge concern is removed.

## Open Questions
- Should viewport coordination live in host or a dedicated interaction contract?
