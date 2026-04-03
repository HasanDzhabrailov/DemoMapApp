# MAP-ARCH-003 — Repackage map host area

## Context
- Host and orchestration-related map code is currently mixed into general `feature/map/impl`.
- This hides the true ownership boundary of parent-level coordination.
- Future host migration requires a dedicated package home before behavior changes.

## Goal
Move host-related map classes into a dedicated package without changing behavior.

## Non-Goals
- No host behavior redesign.
- No removal of `MapScreenRouterBridge`.
- No contract split.
- No navigation migration.

## User Scenarios
- Developer can identify host-level map code by package.
- Later host migration works on a dedicated area without package noise.

## Acceptance Criteria
- [ ] Host-related classes are moved into a dedicated host package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.
- [ ] Package ownership of host orchestration becomes explicit.

## Constraints
- Follow `AGENTS.md`
- Structural move only.
- No production behavior changes.
- Keep change scoped to host-related map files.

## Success Metrics
- Parent orchestration code is no longer mixed with unrelated map feature code.
- Later host refactor tickets can target a dedicated package area.

## Open Questions
- Which exact classes belong to host now versus later mapscreen or feature packages?
- Should `MapScreenRouterStateMappers` stay temporary in host area or be split later?
