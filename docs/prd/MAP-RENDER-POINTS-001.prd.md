# MAP-RENDER-POINTS-001 — Move points/lines/polygons rendering to drawing feature

## Context
- Points, lines, polygons rendering currently centralized in `feature.map.render`.
- AGENTS.md requires each map feature to own only its own sources/layers.
- StyleApplierConstants holds IDs for all features centrally.

## Goal
Move points, lines, polygons rendering ownership to drawing feature module.

## Non-Goals
- No changes to rendering behavior
- No changes to map infrastructure
- No changes to Style Spec

## User Scenarios
- Drawing feature owns its sources and layers
- Other features cannot interfere with drawing layers
- Layer IDs are stable and feature-scoped

## Acceptance Criteria
- [ ] Drawing feature manages its own source/layer IDs
- [ ] Point/line/polygon appliers moved to drawing feature
- [ ] StyleApplierConstants no longer contains drawing-related IDs
- [ ] Feature click handling for drawing features works
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Feature uses only its own sources/layers
- Stable source/layer IDs
- Layer insertion order explicit

## MCP Constraints
- Use only `maplibre_android_docs` MCP
- Allowed: `Style`, `Source`, `GeoJsonSource`, `SymbolLayer`, `LineLayer`, `FillLayer`
- Query-first retrieval: search -> select -> fetch section

## Success Metrics
- Drawing rendering is feature-owned
- Central constants file reduced

## Open Questions
- How to handle feature click registration across multiple features?
- Should drawing feature expose public API for layer IDs?
