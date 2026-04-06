# ARCH-CLEANUP-MAP-003 — Move viewport command routing out of map host glue

## Context
- `DefaultMapHostComponent` still routes viewport commands between viewport, location, ruler, and router state.
- Command source tracking and consumption remain coupled to host orchestration logic.

## Goal
Reduce viewport command routing glue in `DefaultMapHostComponent`.

## Non-Goals
- No unrelated map cleanup.
- No broad rewrite of viewport, location, or ruler feature stores.

## User Scenarios
- Viewport commands still originate from the same features and are consumed correctly.
- Host owns less command orchestration logic.

## Acceptance Criteria
- [ ] Viewport command source tracking/consumption is no longer manually coordinated in `DefaultMapHostComponent` beyond minimal delegation.
- [ ] Command ownership is moved to a bounded parent/store path.
- [ ] Project compiles and relevant tests pass.

## Constraints
- Keep scope limited to viewport command routing cleanup in map area.
- Preserve current command behavior for viewport, location, and ruler flows.

## Success Metrics
- Viewport command glue in map host is reduced.

## Open Questions
- Can current command source handling stay in the existing router store without growing it into another god-store?
