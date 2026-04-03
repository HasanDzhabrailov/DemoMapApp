# MAP-ARCH-004 — Repackage map screen area

## Context
- Renderer-facing map screen logic and contracts are currently entangled with broader map implementation code.
- `DefaultMapScreenComponent` is intended to become map-only later, but it needs an explicit package boundary first.
- Future modularization requires a dedicated mapscreen area.

## Goal
Move map-screen-owned classes into a dedicated package without changing behavior.

## Non-Goals
- No reduction of `DefaultMapScreenComponent` responsibilities yet.
- No host introduction yet.
- No bridge removal.
- No navigation migration.

## User Scenarios
- Developer can identify map-screen-owned code by package.
- Later map-only extraction works on a dedicated area.

## Acceptance Criteria
- [ ] Map-screen-owned classes are moved into a dedicated mapscreen package.
- [ ] Imports are updated and project compiles.
- [ ] Behavior remains unchanged.
- [ ] Package boundary for renderer-facing map screen logic is explicit.

## Constraints
- Follow `AGENTS.md`
- Structural move only.
- No behavior change.
- Keep scope limited to map-screen-owned files.

## Success Metrics
- Map-screen code has a dedicated package boundary ready for later isolation.
- Later ticket can reduce `DefaultMapScreenComponent` responsibility without mixing package-move noise.

## Open Questions
- Should `MapRenderModelMapper` move now with mapscreen, or later with renderer contract cleanup?
- Should feature-click mapping stay in mapscreen or become shared contract code later?
