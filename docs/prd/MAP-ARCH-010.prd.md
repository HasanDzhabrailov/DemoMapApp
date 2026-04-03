# MAP-ARCH-010 — Introduce map host component with child contexts

## Context
- Current `DefaultMapScreenComponent` creates multiple child components with the same `ComponentContext`.
- Official Decompose guidance requires separate child contexts or navigation-managed children.
- A dedicated parent host component is needed so the component tree follows Decompose ownership rules.

## Goal
Introduce a host component that owns immediate child components using proper child contexts.

## Non-Goals
- No full removal of `MapScreenRouterBridge`.
- No full reduction of `DefaultMapScreenComponent` responsibilities yet.
- No contract split yet.

## User Scenarios
- Parent component owns immediate children through correct Decompose child boundaries.
- Future feature additions attach as child components without expanding one root class.

## Acceptance Criteria
- [ ] A dedicated host component is introduced for map feature composition.
- [ ] Each immediate child component is created with its own `childContext(key)` or official Decompose navigation.
- [ ] `DefaultMapScreenComponent` is no longer responsible for constructing all feature children directly.
- [ ] Project compiles and existing behavior remains functionally equivalent.

## Constraints
- Follow `AGENTS.md`
- Use official Decompose child ownership rules.
- Keep behavior changes minimal and scoped.

## Success Metrics
- Map component tree becomes structurally compliant with Decompose guidance.
- Future feature composition no longer depends on one oversized parent class.

## Open Questions
- Should host own a store now, or remain orchestration-only unless a real host responsibility emerges?
