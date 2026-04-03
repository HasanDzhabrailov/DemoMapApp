# MAP-ARCH-016 — Remove UI impl-cast and depend on contracts

## Context
- `MapScreenContent` currently casts API component to impl-specific `MapScreenUiComponent`.
- UI should depend on component contracts, not implementation casts.

## Goal
Remove implementation casting from UI and make UI depend on explicit contracts.

## Non-Goals
- No full split of `MapScreenComponent` yet.
- No broad UI redesign.

## User Scenarios
- UI can render from stable contracts without knowing implementation types.

## Acceptance Criteria
- [ ] UI no longer casts `MapScreenComponent` to implementation-specific `MapScreenUiComponent`.
- [ ] Required child contracts are exposed through stable contract boundaries.
- [ ] Project compiles and behavior remains functionally equivalent.

## Constraints
- Keep UI behavior unchanged.
- Do not introduce new god-contracts.

## Success Metrics
- UI is contract-driven rather than impl-driven.

## Open Questions
- Should host contract expose children directly or via a dedicated UI contract?
