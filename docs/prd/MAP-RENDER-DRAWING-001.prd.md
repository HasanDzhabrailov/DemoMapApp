# MAP-RENDER-DRAWING-001 — Move drawing preview rendering to drawing feature

## Context
- Drawing preview (line/polygon while drawing) centralized in `feature.map.render`.
- Drawing feature should own its preview rendering.

## Goal
Move drawing preview rendering ownership to drawing feature module.

## Non-Goals
- No changes to drawing preview behavior
- No changes to map infrastructure

## Acceptance Criteria
- [ ] Drawing feature manages preview source/layer IDs
- [ ] DrawingPreviewApplier moved to drawing feature
- [ ] StyleApplierConstants no longer contains preview IDs
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Feature owns its layers

## MCP Constraints
- Use only `maplibre_android_docs` MCP

## Success Metrics
- Drawing preview is feature-owned
