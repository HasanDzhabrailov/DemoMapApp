# MAP-ARCH-017 — Split flat MapScreenComponent contract

## Context
- `MapScreenComponent` currently contains a large flat list of feature intents.
- New features would continue expanding one central contract.

## Goal
Replace the flat screen contract with narrower host and child-oriented contracts.

## Non-Goals
- No broad UI redesign.
- No unrelated package cleanup.

## User Scenarios
- A new feature adds its own contract rather than expanding a giant interface.

## Acceptance Criteria
- [ ] Flat screen contract is narrowed or split.
- [ ] Child and host contracts are explicit.
- [ ] Project compiles and behavior remains functionally equivalent.

## Constraints
- Do not replace one giant interface with another giant interface.

## Success Metrics
- Adding new features no longer requires expanding one oversized contract.

## Open Questions
- What is the minimal host contract UI actually needs?
