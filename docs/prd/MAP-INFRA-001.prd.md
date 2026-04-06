# MAP-INFRA-001 — Move base map infrastructure to :map module

## Context
- MapLibre infrastructure (MapView, lifecycle, style loading) currently in `feature.map.render`.
- AGENTS.md requires `map` module to own only base infrastructure.
- Feature-specific rendering should be in feature modules.

## Goal
Move base MapLibre infrastructure to dedicated `:map` module.

## Non-Goals
- No changes to feature-specific rendering (points, lines, etc.)
- No changes to business logic
- No changes to Style Spec usage (only Android API allowed)

## User Scenarios
- Base map infrastructure is reusable
- Feature modules depend on map infrastructure, not own it

## Acceptance Criteria
- [ ] `:map` module created with base infrastructure
- [ ] `MapRenderer`, `MapViewHolder`, lifecycle binding moved to :map
- [ ] Base callbacks (onMapReady, onStyleLoaded) in :map
- [ ] Feature rendering still works through Style access
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Map module is infrastructure only
- No business logic in map module
- Use only MapLibre Android API docs for reference

## MCP Constraints
- Use only `maplibre_android_docs` MCP for MapLibre questions
- Allowed sources: `https://maplibre.org/maplibre-native/android/api/` only
- Forbidden: Style Spec, Native book, examples, GitHub issues
- Prefer class pages (`MapView`, `Style`) over package pages

## Success Metrics
- :map module has clear infrastructure responsibility
- Features work through Style, not direct MapView access

## Open Questions
- Should Style access be wrapped or direct?
- How to handle feature layer ordering with multiple contributors?
