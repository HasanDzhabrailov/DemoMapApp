# MAP-ARCH-008 — Repackage ruler feature

## Context
- Ruler feature code should be isolated for future modularization and responsibility ownership.

## Goal
Move ruler feature code into a dedicated package subtree without changing behavior.

## Non-Goals
- No ruler behavior redesign.
- No host or bridge changes.

## User Scenarios
- Developer can work on ruler in an isolated package.

## Acceptance Criteria
- [ ] Ruler-owned classes are moved into a dedicated ruler package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.

## Constraints
- Structural move only.
- No production behavior change.

## Success Metrics
- Ruler is isolated as a future module candidate.

## Open Questions
- Which ruler overlay UI file belongs to the ruler package?
