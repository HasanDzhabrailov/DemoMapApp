# MAP-ARCH-006 — Repackage drawing feature

## Context
- Drawing feature code should be isolated for future modularization and responsibility ownership.

## Goal
Move drawing feature code into a dedicated package subtree without changing behavior.

## Non-Goals
- No drawing behavior redesign.
- No host or bridge changes.

## User Scenarios
- Developer can work on drawing in an isolated package.

## Acceptance Criteria
- [ ] Drawing-owned classes are moved into a dedicated drawing package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.

## Constraints
- Structural move only.
- No production behavior change.

## Success Metrics
- Drawing is isolated as a future module candidate.

## Open Questions
- Which drawing overlay UI files belong to the drawing package?
