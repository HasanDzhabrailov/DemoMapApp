# MODULES-001 — Create Gradle modules for map feature

## Context
- Currently only `:composeApp` module exists.
- AGENTS.md requires modular structure: `feature:<name>:api`, `feature:<name>:impl`.
- Cannot properly enforce boundaries without real modules.

## Goal
Create `:feature:map:api` and `:feature:map:impl` modules with proper dependencies.

## Non-Goals
- No code behavior changes
- No changes to business logic
- No changes to rendering (will be handled in separate tickets)

## User Scenarios
- Map feature has clear module boundaries
- API/impl split enables independent testing
- Other features can depend only on map API

## Acceptance Criteria
- [ ] `:feature:map:api` module created with public contracts
- [ ] `:feature:map:impl` module created with implementations
- [ ] Proper dependency direction: impl -> api, composeApp -> impl
- [ ] Project compiles and tests pass
- [ ] No code duplication

## Constraints
- Follow `AGENTS.md`
- Respect existing module boundaries
- api module contains only interfaces and models
- impl module contains Store, Component, UI, logic

## Success Metrics
- Build successful with new modules
- Clear dependency graph

## Dependencies
- Must be done after MAP-API-001 (narrow interfaces) - need stable API contracts
- Recommended after MAP-API-002 (consolidate model) - simpler module boundaries

## Open Questions
- Should we create `:map` (infrastructure) module in same ticket or separate?
- How to handle commonMain/androidMain/jvmMain split in new modules?
- Should we extract one module at a time or all at once?
