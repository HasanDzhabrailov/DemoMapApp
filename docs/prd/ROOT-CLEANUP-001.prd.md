# ROOT-CLEANUP-001 — Remove trivial RootComponentFactory helper

## Context
- `root/impl/RootComponentFactory.kt` is a single-function wrapper that only returns `DefaultRootComponent(componentContext)`.
- It adds indirection without policy, configuration, or lifecycle ownership.
- This is template-level scaffolding that should be removed.

## Goal
Remove `RootComponentFactory.kt` and create `DefaultRootComponent` directly from platform entry points.

## Non-Goals
- No changes to RootComponent behavior or navigation logic
- No changes to component creation beyond removing the factory wrapper
- No dependency injection framework introduction

## User Scenarios
- Entry points (MainActivity, Main) directly instantiate DefaultRootComponent
- Code is simpler with one less indirection layer

## Acceptance Criteria
- [ ] `RootComponentFactory.kt` file is deleted
- [ ] `MainActivity.kt` creates `DefaultRootComponent` directly
- [ ] `Main.kt` (JVM) creates `DefaultRootComponent` directly
- [ ] Project compiles and tests pass
- [ ] No references to `createRootComponent` remain in codebase

## Constraints
- Follow `AGENTS.md`
- Keep the change minimal
- Both Android and JVM entry points must be updated

## Success Metrics
- Root area has one less helper/glue class
- Factory indirection is eliminated

## Open Questions
- Should DefaultRootComponent constructor remain internal or become public?
