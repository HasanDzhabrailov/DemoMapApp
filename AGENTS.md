# AGENTS.md

## Scope

- Use the minimal workflow only.
- Current ticket is stored in `docs/.active_ticket`.
- Before making changes, read:
- `docs/.active_ticket`
- `docs/prd/<ticket>.prd.md`
- `docs/plan/<ticket>.md`
- `docs/tasklist/<ticket>.md`
- Work only within the current ticket and its business area.
- Changes in shared modules are allowed only when minimally required by the current ticket.
- Repository state is the source of truth.
- If docs and code differ:
- report mismatch
- code remains the source of truth
- update docs only if the current ticket or tasklist explicitly requires it
- do not expand scope only to align outdated docs with code
- If a change is not required for the current task: do not do it.

## Operating Principle

- Assumptions are not evidence.
- Only verified facts can be used to declare completion.
- Prefer the simplest correct solution.
- Do not introduce abstractions without a concrete need.
- Deviating from a rule is allowed only if the current ticket scope requires it and the justification is explicit, concrete, and verifiable from code.

## Stack

- Kotlin Multiplatform (KMP)
- Kotlin
- Jetpack Compose
- Decompose
- MVIKotlin Store
- Coroutines + Flow
- MapLibre via `AndroidView`

## Module Structure

- The app must be modular.
- Each feature must be isolated and independently connectable.
- Each module must have one clear responsibility.
- Use `feature:<name>:impl` by default.
- Use `feature:<name>` only when the repo already uses it as the stable feature root module.
- Create `feature:<name>:api` only if another module depends on this feature.
- `feature:<name>:impl` contains Store, Component, UI, internal business logic, and feature-scoped repositories.
- `feature:<name>:api` contains only minimal interfaces, minimal models, and entry points needed by other modules.
- `core` is for shared non-UI base code, `ui` for reusable UI components, `map` for base map infrastructure only.
- Do not move feature-specific code into shared modules unless at least two different features depend on the same logic.
- Do not create shared interfaces for a single implementation.
- Do not add layers for future flexibility.

## Feature Architecture

- Each screen must follow `Component -> Store -> UI`.

### Component

- Owns lifecycle, Store creation, navigation wiring, and dependency passing.
- Must not contain business logic.
- A complex screen may use a parent Component with child Components.
- Each child Component must own its own Store.
- Store instances must not be shared between parent and child Components or between siblings.

### Store

- One Component = one Store.
- One Store = one responsibility zone.
- Store is the single source of truth for screen state.
- Store handles intents, messages, labels, and state updates.
- Create Store directly in the Component or through a simple local factory.

### Reducer

- Reducer must be pure.
- Reducer may only transform state.
- Reducer must not do IO, access time, call platform APIs, navigate, or log.

### Executor

- Executor is an orchestration layer, not a container for all business logic.
- Executor owns side effects, repository calls, dispatcher switching, time/platform access required by feature behavior, and message/label publishing.
- Keep only simple, local, single-use logic in Executor.
- Extract logic from Executor to `feature:<name>:impl` if any is true:
- function is longer than 30 lines
- branching depth is greater than 2
- logic orchestrates multiple repositories
- logic is hard to explain briefly and precisely
- logic is likely to be reused
- Loss of readability inside Executor is sufficient reason to extract feature-local logic into `feature:<name>:impl`.

### UI and State

- UI uses only Jetpack Compose.
- UI renders state and emits intents.
- UI must not contain business logic, access repositories directly, own screen state, mutate state directly, or launch uncontrolled coroutines.
- Use `StateFlow` for screen state and Labels for one-time events.
- State must contain persistent UI data only.
- Refactor state if it has more than 7 top-level fields or nesting depth greater than 2.
- Do not keep heavy derived data in State when it can be produced outside persistent state.

## Dependencies and KMP Boundaries

- Use manual dependency management only.
- Platform composition root must create platform dependencies.
- Android APIs are allowed only in the platform composition root and platform-specific modules.
- Pass dependencies via constructors.
- Use Decompose composition for dependency passing.
- Keep dependencies minimal, explicit, and local.
- Small feature-local factories or dependency holders are allowed only inside the current feature scope.
- Forbidden: Hilt, Koin, any DI framework, Service Locator, global dependency containers, passing `Context` below the platform composition root.
- Put into `commonMain`: Store, Executor, Reducer, feature-local business logic, platform-agnostic repositories.
- Put into platform-specific source sets: Android APIs, database builders, MapLibre adapters, permissions, storage.
- Forbidden: Android APIs in `commonMain`, platform APIs in reducer, platform APIs in feature-local logic, `expect` / `actual` for pure logic, `expect` / `actual` instead of DI.
- Use `expect` / `actual` only for real platform differences such as clock, storage, or database driver.
- Keep application-scope objects at application scope only; keep feature logic at feature scope; keep Store and UI-level objects at Component scope.

## Data, Errors, and Use Cases

- Persistence must not leak into UI or reducer.
- Repositories hide data sources and are used from executor-side logic, not directly from UI.
- Complex or reusable feature business logic belongs in `feature:<name>:impl`.
- Raw exceptions must not cross reducer or UI boundaries.
- Errors must be normalized on the executor side.
- Persistent user-visible errors go to State.
- One-time error effects go to Labels.
- Reducer and UI must not perform exception mapping.
- Use case is forbidden by default.
- Use case is an exception, not a layer.
- No domain layer.
- Choose logic placement in this order: Executor -> `feature:<name>:impl` -> use case.
- Use case is allowed only if at least one condition is true and the benefit is concrete:
- orchestration of multiple repositories in one business operation
- same business logic reused by different Stores or different features
- logic is too complex for Executor and still not readable after extraction into `feature:<name>:impl`
- logic must be tested independently outside Store because Store-level tests are not enough
- Use case is forbidden for a single repository call, simple mapping/filtering/formatting, single-use simple logic, speculative reasons, or Clean Architecture symmetry.
- Use case must not depend on Android APIs, UI, Component, Store, Compose, or navigation.
- Use case must not introduce an interface unless there is a second real consumer.

## Map Rules

- MapLibre is rendering infrastructure only.
- Follow the `Style -> Sources -> Layers` model strictly.
- The `map` module is responsible only for `MapView`, map lifecycle, style loading, style access, and base callbacks such as `onMapReady` and `onStyleLoaded`.
- The `map` module must not contain business logic, concrete map feature logic, or feature-specific layers/sources.
- Each map feature must be a separate module that works only through `Style`, adds only its own sources/layers, manages only its own layers/sources, does not interfere with other features, and does not keep direct `MapView` references.
- Perform map feature operations only after `Style` is ready.
- Map feature modules must use stable source ids and layer ids.
- Layer insertion order relative to existing layers must be explicit when rendering depends on that order.
- Do not create one big `MapComponent` for all map behavior.

## Workflow, Docs, and Tests

- Design screen logic in this order: Store state/intents/messages/labels -> Executor side effects -> Component wiring -> UI -> platform adapters.
- Before writing code, decide where the logic belongs.
- If logic is simple, local, and used once, keep it in Executor.
- If logic is complex, reusable, or hard to read inside Executor, move it to `feature:<name>:impl`.
- Use a use case only if the rules above explicitly allow it.
- Do not move logic out of Executor blindly.
- Reducer logic should be unit-testable as pure logic.
- Critical executor paths should be tested when behavior is non-trivial.
- Use Store-level tests only when reducer-level or executor-level tests are not sufficient.
- Use repo-local OpenCode MCP for official Android/Kotlin docs.
- Always use search first, then choose one best page, then fetch only the needed section.
- Android docs must come from `developer.android.com`, Kotlin docs from `kotlinlang.org`.
- Use `android_docs_search`, `kotlin_docs_search`, and `official_docs_fetch`.
- If direct `opencode run` does not work in this environment, use `tools/opencode_attach_run.py`.

## Verification and Completion

- Never mark task as DONE without verification.
- Never claim success without command and result.
- If verification is not possible, state why.
- If only part is verified, report PARTIALLY COMPLETE.
- Task is DONE only if implementation matches ticket scope, module boundaries remain clear, `Component -> Store -> UI` is preserved, one Component owns one Store, reducer is pure, executor handles side effects, error handling follows State/Labels rules, map changes follow `Style -> Sources -> Layers`, map ids/order are explicit when required, and verification was executed and reported.
- After completion, update `docs/tasklist/<ticket>.md`.
- Reports must include: Result, Files Changed, Verification, Limitations, Risks, Suggested Commit Message.
- Completion statuses: DONE, PARTIALLY COMPLETE, BLOCKED.

## Errors and Secrets

- Never hardcode tokens, credentials, or secrets.
- Follow project logging patterns.

## Output Preference

- Be concise.
- Be precise.
- No speculative improvements.
- No workflow inventions unless requested.
