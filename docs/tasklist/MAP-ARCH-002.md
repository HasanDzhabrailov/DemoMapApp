# Tasklist - MAP-ARCH-002

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope: design-only

- [x] Define top-level target packages
- [x] Define ownership of each package
- [x] Define allowed dependency directions
- [x] Define rules for shared map code

- [x] Verify package map covers current map responsibilities

- [x] Update this tasklist
- [x] Prepare commit message

## Notes
- Doc/code mismatch: current repo still uses `feature/map/impl` as the main implementation area and already has supporting `feature/map/ui` and `feature/map/render` packages. `MAP-ARCH-002` documents the future ownership map for incremental migration; it does not reflect the physical package tree yet.

## Expected Results
| Check | Status |
|-------|--------|
| Target package map is approved | DONE |
| Dependency rules are documented | DONE |
