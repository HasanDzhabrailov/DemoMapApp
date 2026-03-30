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
- Design and refactor by responsibility ownership and reason to change, not by file size.
- Split files only when responsibilities diverge or would otherwise create mixed ownership.
- New screen logic must use Decompose component + MVIKotlin Store as the default structure.
- Decompose component is a thin lifecycle holder, Store holder, and navigation bridge only.
- MVIKotlin Store is the center of screen logic and state transitions.
- Reducer must stay pure: no IO, time, randomness, platform calls, navigation, or mutation outside returned state.
- Executor owns side effects and orchestration: use cases, clocks, ids, platform requests, and label publication.
- Labels are one-shot outputs only; do not move persistent screen state into labels.
- UI/composables render state and send intents only.
- Do not put business decisions, data shaping for logic, or side effects into UI code.
- Renderer/adapters are platform rendering only; keep MapLibre and Android view glue out of business logic.
- Prefer introducing small focused collaborators over growing god components, god stores, or god renderers.
- Do not add compatibility layers that duplicate Store state in component/UI unless a boundary strictly requires it.
- Shared/core changes are allowed only when the current ticket cannot be completed cleanly without them.
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
- Do not calculate business state in composables when the Store or a focused collaborator can own it.
- Follow Compose best practices.
- Avoid unnecessary recompositions, allocations, jank, and heavy work on the main thread.

## Map Rules

- MapLibre is used for map rendering only.
- Keep MapLibre integration Android-specific.
- Do not place business logic inside map rendering code.

## Feature Workflow Rules

- Start new screen work from Store intent/state/label design, then wire component, then UI, then renderer/platform adapters.
- Keep component APIs intent-shaped; avoid exposing business logic helpers from composables or renderers.
- When adding behavior, first decide whether it belongs to reducer, executor, label handling, or renderer before editing files.
- If a file starts owning multiple responsibilities, extract the narrower responsibility instead of adding more branches.

## Anti-Patterns

- Fake-MVI: component or UI mutating screen state directly instead of going through Store intents/messages.
- Reducer side effects: clocks, ids, logging, navigation, platform access, or use case execution in reducer code.
- UI business logic: composables deciding feature behavior beyond trivial presentation concerns.
- Renderer business logic: MapLibre click/render code deciding product rules or editing feature state.
- God files created only because "it is easier here" when ownership clearly differs.

## Background Work Rules

- Use WorkManager / Foreground Service / Service only when clearly justified.
- Keep business logic out of orchestration/background framework classes.

## Persistence

- Use Room only where persistence is required by the current task.
- Keep persistence concerns out of UI code.

## Formatting, Static Analysis, and Tests

Use the existing project setup for formatting and verification.

Preferred commands, if configured in this repository:

- `./gradlew ktlintFormat` — auto-format code to fix style violations
- `./gradlew ktlintCheck` — verify code style compliance
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
