# MAP-RENDER-LOCATION-001 — Move current location rendering to location feature

## Context
- Current location marker rendering centralized in `feature.map.render`.
- Location feature should own its visual representation.

## Goal
Move current location rendering ownership to location feature module.

## Non-Goals
- No changes to location behavior
- No changes to map infrastructure

## Acceptance Criteria
- [ ] Location feature manages its own source/layer IDs
- [ ] CurrentLocationStyleApplier moved to location feature
- [ ] StyleApplierConstants no longer contains location IDs
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Feature owns its layers

## MCP Constraints
- Use only `maplibre_android_docs` MCP
- Allowed: `Style`, `Source`, layer classes

## Success Metrics
- Location rendering is feature-owned

## Open Questions
- Should location layer be below or above other feature layers?
