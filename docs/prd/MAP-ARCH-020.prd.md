# MAP-ARCH-020 — Migrate remaining ephemeral map flows to Decompose navigation

## Context
- After tools and center marker migration, remaining ephemeral map flows should be reviewed and migrated where navigation semantics apply.

## Goal
Migrate remaining eligible ephemeral map flows to official Decompose navigation.

## Non-Goals
- No repo-wide navigation refactor.

## User Scenarios
- Remaining map dialogs, sheets, or temporary flows follow Decompose navigation consistently.

## Acceptance Criteria
- [ ] Remaining eligible ephemeral map flows use official Decompose navigation.
- [ ] Ineligible simple state remains state and is not over-migrated.
- [ ] Project compiles and tests pass.

## Constraints
- Use navigation only where flow semantics justify it.

## Success Metrics
- Map feature uses navigation consistently without unnecessary complexity.

## Open Questions
- Which remaining flows should stay plain state by design?
