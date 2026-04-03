# MAP-ARCH-007 — Repackage location feature

## Context
- Location feature code should be isolated for future modularization and clear ownership.

## Goal
Move location feature code into a dedicated package subtree without changing behavior.

## Non-Goals
- No location behavior redesign.
- No host or bridge changes.

## User Scenarios
- Developer can work on location in an isolated package.

## Acceptance Criteria
- [ ] Location-owned classes are moved into a dedicated location package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.

## Constraints
- Structural move only.
- No production behavior change.

## Success Metrics
- Location is isolated as a future module candidate.

## Open Questions
- Which location controls UI file belongs to the location package?
