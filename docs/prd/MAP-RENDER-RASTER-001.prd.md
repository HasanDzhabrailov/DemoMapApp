# MAP-RENDER-RASTER-001 — Move raster tile overlay rendering to tools feature

## Context
- Raster tile overlay rendering centralized in `feature.map.render`.
- Tools feature manages overlay layers and should own their rendering.

## Goal
Move raster tile overlay rendering ownership to tools feature module.

## Non-Goals
- No changes to raster tile behavior
- No changes to map infrastructure

## Acceptance Criteria
- [ ] Tools feature manages overlay source/layer IDs
- [ ] RasterTileStyleApplier moved to tools feature
- [ ] StyleApplierConstants no longer contains raster IDs
- [ ] Project compiles and tests pass

## Constraints
- Follow `AGENTS.md`
- Feature owns its layers
- Layer insertion order preserved

## MCP Constraints
- Use only `maplibre_android_docs` MCP

## Success Metrics
- Raster overlay rendering is feature-owned
