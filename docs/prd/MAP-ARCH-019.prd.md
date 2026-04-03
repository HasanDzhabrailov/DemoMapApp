# MAP-ARCH-019 — Migrate center marker flow to Decompose navigation

## Context
- Center marker menu flow is currently boolean-driven and tied to broader state management.

## Goal
Migrate center marker flow to official Decompose navigation.

## Non-Goals
- No migration of unrelated map flows.

## User Scenarios
- Center marker menu is represented as explicit navigation child flow.

## Acceptance Criteria
- [ ] Center marker flow uses an official Decompose navigation model.
- [ ] Behavior remains functionally equivalent.
- [ ] Project compiles and tests pass.

## Constraints
- Scope only center marker flow.

## Success Metrics
- Center marker flow becomes isolated and scalable.

## Open Questions
- Should center marker be owned by viewport package or mapscreen package after navigation migration?
