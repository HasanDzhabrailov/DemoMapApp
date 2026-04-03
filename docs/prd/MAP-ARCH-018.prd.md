# MAP-ARCH-018 — Migrate tools flows to Decompose navigation

## Context
- Tools-related ephemeral flows currently rely on boolean state and ad-hoc coordination.
- Official Decompose navigation should be used where ephemeral child flows are actual navigation.

## Goal
Migrate tools-related ephemeral flows to official Decompose navigation.

## Non-Goals
- No migration of unrelated map flows.

## User Scenarios
- Tools menu, sheets, and related flows use explicit navigation children.

## Acceptance Criteria
- [ ] Relevant tools flows use official Decompose navigation models.
- [ ] Behavior remains functionally equivalent for users.
- [ ] Project compiles and tests pass.

## Constraints
- Use official Decompose navigation patterns only.
- Scope to tools flows.

## Success Metrics
- Tools ephemeral navigation is explicit and scalable.

## Open Questions
- Which tools flows are best modeled as `ChildSlot` versus retained state?
