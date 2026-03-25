# AGENTS.md

## Scope

Use the minimal workflow only.

Current ticket is stored in `docs/.active_ticket`.
Before making changes, read:

- `docs/.active_ticket`
- `docs/prd/<ticket>.prd.md`
- `docs/plan/<ticket>.md`
- `docs/tasklist/<ticket>.md`

Work only within the current ticket and its business area.
Changes in shared/core modules are allowed only when they are minimally required by the current ticket.

## Stack

- Kotlin Multiplatform (KMP)
- Kotlin
- Decompose
- Jetpack Compose
- Room
- MapLibre via `AndroidView`

## Architecture Rules

- KMP-first. Prefer `commonMain` whenever possible.
- Keep Android-specific code only in `androidMain` / `android`.
- Do not leak platform-specific code into business logic.
- This is a modular super app. Each module owns one business area.
- Use `feature/api/impl` structure.
- There is no separate domain layer.
- Prefer composition, interfaces, delegation, adapters, and manual DI over inheritance.
- Do not introduce `Base*` classes.
- Do not introduce abstract base classes.
- Do not introduce DI frameworks.
- Respect SOLID.
- Respect thread-safety and the Java Memory Model.
- Use lifecycle-aware coroutines and avoid memory leaks.

## UI Rules

- Compose is UI only.
- State must be owned outside composables.
- Composables must emit events only.
- Do not launch side effects from the composable body.
- Follow Compose best practices.
- Avoid unnecessary recompositions, allocations, jank, and heavy work on the main thread.

## Map Rules

- MapLibre is used for map rendering only.
- Keep MapLibre integration Android-specific.
- Do not place business logic inside map rendering code.

## Background Work Rules

- Use WorkManager / Foreground Service / Service only when clearly justified.
- Keep business logic out of orchestration/background framework classes.

## Persistence

- Use Room only where persistence is required by the current task.
- Keep persistence concerns out of UI code.

## Formatting, Static Analysis, and Tests

Use the existing project setup for formatting and verification.

Preferred commands, if configured in this repository:

- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew test`

Before finishing a task, run the smallest relevant verification set for the changed code.

## Errors and Secrets

- Do not hardcode secrets, tokens, or credentials.
- Follow existing project patterns for error handling and logging.
- If there is no existing pattern, keep changes minimal and do not invent a new cross-project error framework.

## Task Execution Rules

- Make the smallest change that satisfies the current task.
- Do not expand scope without explicit need.
- Update `docs/tasklist/<ticket>.md` after completing a task step.
- After changes, provide:
  - a concise diff summary
  - a suggested commit message

## Output Preference

Prefer compact, precise answers.
Do not propose advanced workflow, custom agents, hooks, commands, or JSON config unless explicitly requested.