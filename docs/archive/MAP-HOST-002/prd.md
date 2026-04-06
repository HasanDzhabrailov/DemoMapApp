# MAP-HOST-002 — Remove viewport command routing from DefaultMapHostComponent

## Context
- `DefaultMapHostComponent` routes viewport commands between viewport, location, ruler, and router state.
- Command source tracking and consumption remain coupled to host orchestration.
- This makes host responsible for cross-feature command coordination.

## Goal
Reduce viewport command routing glue in DefaultMapHostComponent by moving ownership to bounded parent store.

## Non-Goals
- No changes to viewport command behavior
- No changes to viewport/location/ruler feature stores
- No changes to command types

## User Scenarios
- Viewport commands originate from features and are consumed correctly
- Host owns minimal command orchestration logic

## Acceptance Criteria
- [ ] Viewport command source tracking/consumption not manually coordinated in host
- [ ] Command ownership in bounded parent store path
- [ ] No behavioral regression in viewport commands
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Preserve command behavior for viewport, location, ruler flows
- Keep scope limited to routing cleanup

## Success Metrics
- Host no longer tracks command sources manually
- Command routing logic is in store/executor

## Open Questions
- Can command handling stay in existing router store without growing it too much?
- Is command consumption UI-side or store-side responsibility?
