# MAP-HOST-003 — Remove component-level business rules from DefaultMapHostComponent

## Context
- `DefaultMapHostComponent.onCenterMarkerClick()` contains business rule: blocks action when drawing mode is active (lines 223-229).
- This is business logic in Component layer, violating AGENTS.md.
- Component should render state and emit intents, not gate actions based on state.

## Goal
Move component-level business rules to parent store/executor layer.

## Non-Goals
- No changes to user-visible behavior (drawing mode still blocks center marker)
- No changes to drawing feature
- No changes to viewport feature

## User Scenarios
- Center marker click behavior remains the same
- Business logic is testable in store/executor, not hidden in component

## Acceptance Criteria
- [ ] `onCenterMarkerClick()` in host has no conditional blocking logic
- [ ] Drawing mode check lives in store/executor
- [ ] Behavior preserved: center marker disabled during drawing
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Component only renders state and emits intents
- Store owns business rules

## Success Metrics
- Component methods are simple delegations
- Business logic is in testable store layer

## Open Questions
- Are there other component methods with embedded business rules?
- Should this be handled via state-driven UI (disabled state) or intent rejection?
