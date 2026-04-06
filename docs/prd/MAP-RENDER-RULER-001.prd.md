# MAP-RENDER-RULER-001 — Move ruler rendering to ruler feature

## Context
- Ruler measurement rendering centralized in `feature.map.render`.
- Ruler feature should own its visual representation.

## Goal
Move ruler rendering ownership to ruler feature module.

## Non-Goals
- No changes to ruler behavior
- No changes to map infrastructure

## Acceptance Criteria
- [ ] Ruler feature manages its own source/layer IDs
- [ ] RulerStyleApplier moved to ruler feature
- [ ] StyleApplierConstants no longer contains ruler IDs
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Feature owns its layers

## MCP Constraints
- Use only `maplibre_android_docs` MCP
- Allowed: `Style`, `Source`, `LineLayer`

## Success Metrics
- Ruler rendering is feature-owned
