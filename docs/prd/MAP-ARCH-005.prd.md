# MAP-ARCH-005 — Repackage tools feature

## Context
- Tools feature code is mixed into broader map implementation structure.
- Future modularization requires tools to have an explicit package boundary.

## Goal
Move tools feature code into a dedicated package subtree without changing behavior.

## Non-Goals
- No tools behavior redesign.
- No navigation migration.
- No host contract changes.

## User Scenarios
- Developer can work on tools feature in an isolated package.

## Acceptance Criteria
- [ ] Tools-owned classes are moved into a dedicated tools package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.

## Constraints
- Structural move only.
- No production behavior change.

## Success Metrics
- Tools is isolated as a future module candidate.

## Open Questions
- Which tools UI files belong with the tools package versus shared map screen UI?
