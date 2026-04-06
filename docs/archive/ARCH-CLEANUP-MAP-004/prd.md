# ARCH-CLEANUP-MAP-004 — Reduce overlay exclusivity orchestration in map host

## Context
- `DefaultMapHostComponent` still coordinates menu dismissal and feature info window clearing across tools, viewport, drawing, and feature selection flows.
- This overlay exclusivity behavior is spread across host click handlers and helper methods like `dismissToolsMenuIfVisible` and `dismissViewportMenuIfVisible`.

## Goal
Reduce overlay exclusivity and dismissal glue in `DefaultMapHostComponent`.

## Non-Goals
- No unrelated map cleanup.
- No visual redesign of map overlays.

## User Scenarios
- Opening one overlay still dismisses conflicting overlays correctly.
- Feature info window visibility remains correct during overlay transitions.
- Host owns less cross-feature UI orchestration logic.

## Acceptance Criteria
- [ ] Overlay exclusivity rules are owned by a bounded parent/store path instead of being spread across host click handlers.
- [ ] `DefaultMapHostComponent` no longer manually coordinates most menu dismissal and feature info window clearing logic.
- [ ] Project compiles and relevant tests pass.

## Constraints
- Keep scope limited to overlay exclusivity cleanup in map area.
- Preserve current overlay behavior.

## Success Metrics
- Cross-feature overlay dismissal glue in map host is reduced.

## Open Questions
- Which overlay exclusivity rules belong in parent state versus child feature ownership?
