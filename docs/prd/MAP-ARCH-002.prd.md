# MAP-ARCH-002 — Define target package map for feature/map

## Context
- `feature/map/impl` currently mixes orchestration, feature logic, renderer-facing logic, mappers, and cross-feature glue.
- Current package layout does not clearly reflect future module boundaries.
- Future refactor work needs a target package map before physical moves start.

## Goal
Define the target package structure and dependency rules for `feature/map` so it can be incrementally migrated toward multi-module readiness.

## Non-Goals
- No file moves.
- No code behavior changes.
- No component creation or removal.
- No navigation migration.

## User Scenarios
- Architect plans package moves from an approved target structure.
- Future map refactor tickets reuse package ownership and dependency rules.

## Acceptance Criteria
- [ ] Target package structure for `feature/map` is defined.
- [ ] Ownership of host, mapscreen, tools, drawing, location, ruler, and viewport areas is documented.
- [ ] Allowed dependency direction between map packages is documented.
- [ ] Rules for when shared code is allowed are documented.
- [ ] Package structure is explicitly aligned with future multi-module extraction.

## Constraints
- Follow `AGENTS.md`
- Use minimal package structure that reflects ownership.
- Avoid premature deep nesting.
- Respect existing map business area scope only.

## Success Metrics
- Each map responsibility has a clear future package home.
- Future package-move tickets can be generated directly from this design.

## Open Questions
- Should `mapscreen` be under `feature/map/mapscreen` or `feature/map/mapscreen/impl` from the start?
- Which current mapper and resolver classes are truly shared versus temporary glue?
