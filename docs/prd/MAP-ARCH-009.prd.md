# MAP-ARCH-009 — Repackage viewport feature

## Context
- Viewport feature code should be isolated for future modularization and responsibility ownership.

## Goal
Move viewport feature code into a dedicated package subtree without changing behavior.

## Non-Goals
- No viewport behavior redesign.
- No host or bridge changes.

## User Scenarios
- Developer can work on viewport in an isolated package.

## Acceptance Criteria
- [ ] Viewport-owned classes are moved into a dedicated viewport package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.

## Constraints
- Structural move only.
- No production behavior change.

## Success Metrics
- Viewport is isolated as a future module candidate.

## Open Questions
- Which center marker UI files belong to the viewport package?
