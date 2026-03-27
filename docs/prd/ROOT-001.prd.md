# ROOT-001 — Create map component through RootComponent in Decompose

## Context
The current map component creation flow must be aligned with Decompose root-driven composition.

Map-related component creation should be controlled by the root layer instead of being instantiated locally in UI or outside the root composition path.

This keeps component lifecycle, ownership, and navigation composition consistent with the project architecture.

## Goal
Refactor map component creation so the map component instance is created through `RootComponent` / `DefaultRootComponent` and passed down through the root composition flow.

## Non-Goals
- No business logic changes
- No redesign of map rendering
- No MapLibre API expansion
- No direct MapLibre usage in shared business logic
- No broad navigation refactor beyond the minimal root-driven creation path
- No new DI framework
- No Base classes
- No abstract base classes

## User Scenarios
- Scenario 1: A user opens the app and reaches the map screen as before.
- Scenario 2: A developer can trace map component ownership from the root layer.
- Scenario 3: Component lifecycle is controlled by Decompose root composition instead of local UI construction.

## Acceptance Criteria
- [ ] Map component instance is created through `RootComponent` / `DefaultRootComponent`
- [ ] UI does not directly instantiate the map component
- [ ] Component ownership and lifecycle are controlled from the root layer
- [ ] Existing map rendering behavior remains unchanged
- [ ] No business logic is added to map rendering code
- [ ] No Android-specific MapLibre code leaks into shared business logic

## Constraints
- Follow `AGENTS.md`
- Keep the change minimal
- Respect KMP-first architecture
- Respect existing `feature/api/impl` and root module boundaries
- Keep MapLibre as render layer only
- Keep Compose UI-only

## Success Metrics
- Map screen still works
- Component creation path is root-owned and explicit
- Lifecycle management is clearer and consistent with Decompose
- Verification commands pass

## Open Questions
- Should the root expose the map component directly or as part of a root child model?
- Is a small adapter needed between root-owned component and UI entry point?