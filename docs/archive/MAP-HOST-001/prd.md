# MAP-HOST-001 — Extract overlay exclusivity rules from DefaultMapHostComponent

## Context
- `DefaultMapHostComponent` coordinates menu dismissal and feature info window clearing across tools, viewport, drawing, and feature selection flows.
- Overlay exclusivity behavior is spread across host click handlers and helper methods.
- This violates AGENTS.md rule that Component should not contain business logic.

## Goal
Move overlay exclusivity rules from host Component to a bounded parent store/executor.

## Non-Goals
- No visual redesign of map overlays
- No changes to user-visible behavior
- No changes to child feature stores

## User Scenarios
- Opening one overlay still dismisses conflicting overlays correctly
- Feature info window visibility remains correct during overlay transitions
- Host owns less cross-feature UI orchestration logic

## Acceptance Criteria
- [ ] Overlay exclusivity rules live in parent store/executor, not in host Component methods
- [ ] `DefaultMapHostComponent` delegates to store instead of manual coordination
- [ ] No regression in overlay dismissal behavior
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Component stays focused on wiring and lifecycle
- Store owns cross-feature coordination logic

## Success Metrics
- Host Component line count reduced
- Overlay logic is testable in store/executor
- No behavioral regression

## Open Questions
- Should overlay state be part of existing MapRouterStore or separate?
- Which overlay exclusivity rules are truly cross-feature vs child-owned?
