# MAP-ARCH-011 — Reduce DefaultMapScreenComponent to map-only

## Context
- Current `DefaultMapScreenComponent` knows about tools, drawing, location, ruler, viewport, and router orchestration.
- Map screen component should only own map rendering-facing responsibilities.

## Goal
Reduce `DefaultMapScreenComponent` so it owns only map-specific responsibilities.

## Non-Goals
- No complete bridge removal yet.
- No full API split yet.

## User Scenarios
- Map screen component can evolve independently from other features.

## Acceptance Criteria
- [ ] `DefaultMapScreenComponent` owns only map-specific responsibilities.
- [ ] Feature-specific orchestration is moved out of map screen component.
- [ ] Project compiles and behavior remains functionally equivalent.

## Constraints
- Keep renderer-facing behavior stable.
- Do not reintroduce central orchestration into the mapscreen package.

## Success Metrics
- Map screen becomes independent from non-map feature ownership.

## Open Questions
- Which feature-click and info-window responsibilities should remain in mapscreen versus host?
