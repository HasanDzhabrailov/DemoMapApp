# OTHER-CLEANUP-001 — Remove leftover KMP template scaffolding

## Context
- `Greeting.kt`, `Platform.kt` and platform-specific Platform implementations remain in the root package.
- These are template scaffolding files not part of current business logic.
- They create noise in the root package and may confuse new developers.

## Goal
Remove template scaffolding files that are not used by production code.

## Non-Goals
- No changes to production business logic
- No changes to platform abstractions that ARE used
- Do not remove files if they have actual callers

## User Scenarios
- Repository contains only production-relevant code
- Root package is cleaner

## Acceptance Criteria
- [ ] Verify `Greeting.kt` has no callers and delete if true
- [ ] Verify `Platform.kt` and implementations have no callers and delete if true
- [ ] Project compiles and tests pass
- [ ] No regression in app functionality

## Constraints
- Follow `AGENTS.md`
- Verify no dependencies before deletion
- Keep the change minimal

## Success Metrics
- Root package contains only relevant code
- Template scaffolding is removed

## Open Questions
- Are there any hidden dependencies on these files (e.g., tests, previews)?
