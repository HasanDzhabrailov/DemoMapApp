# MAP-ARCH-001 — Update architecture rules and AGENTS.md

## Context
- Current map architecture allows growth of central classes like `DefaultMapScreenComponent` and `MapScreenRouterBridge`.
- Current code reuses one `ComponentContext` across multiple child components, which conflicts with official Decompose guidance.
- Current structure does not explicitly enforce per-feature component/store ownership or package boundaries aligned for future modularization.

## Goal
Formalize architecture rules for feature decomposition, Decompose child ownership, navigation usage, package boundaries, and ticket sizing.

## Non-Goals
- No production code refactor in this ticket.
- No package moves.
- No component rewiring.
- No navigation migration.

## User Scenarios
- Architect creates new feature ticket and follows explicit component/store/package rules.
- Future refactor tickets use the same decomposition rules and avoid new god-classes.

## Acceptance Criteria
- [ ] `AGENTS.md` explicitly requires one component per feature responsibility.
- [ ] `AGENTS.md` explicitly requires one store per feature component.
- [ ] `AGENTS.md` explicitly requires separate `childContext(key)` or official Decompose navigation for child components.
- [ ] `AGENTS.md` explicitly states that package boundaries must support future multi-module extraction.
- [ ] `AGENTS.md` explicitly forbids growth of central god-components and bridge/glue orchestration classes without clear ownership.
- [ ] `AGENTS.md` explicitly defines ticket sizing rules so oversized refactors must be split.

## Constraints
- Follow `AGENTS.md`
- Use official Decompose documentation as source of truth for component and navigation rules.
- Keep the change minimal.
- Do not change production behavior.

## Success Metrics
- Architecture constraints become explicit and reusable across future tickets.
- Future refactor tickets have a stable rules baseline.

## Open Questions
- Should package naming convention be enforced globally or only for new or changed business areas?
- Should helper or glue prohibition be global or only enforced when responsibility can be owned by component, store, or navigation?
