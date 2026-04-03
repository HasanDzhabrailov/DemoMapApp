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

Repository state is the source of truth.
Documentation may be outdated.

If docs and code differ:
- report mismatch
- do not assume docs are correct

If a change is not required for the current task:
- do not do it

---

## Operating Principle

Assumptions are not evidence.
Only verified facts can be used to declare completion.

---

## Stack

- Kotlin Multiplatform (KMP)
- Kotlin
- Decompose
- Jetpack Compose
- Room
- MapLibre via `AndroidView`

---

## Architecture Rules

- KMP-first. Prefer `commonMain` whenever possible.
- Keep Android-specific code only in `androidMain` / `android`.
- Do not leak platform-specific code into business logic.

- This is a modular super app. Each module owns one business area.
- Use `feature/api/impl` structure.
- Package boundaries in changed code must support future multi-module extraction.
- New or changed packages must align with ownership boundaries so a feature can be moved without unrelated code moves.
- There is no separate domain layer.

- Design and refactor by responsibility ownership and reason to change, not by file size.
- Split files only when responsibilities diverge or would otherwise create mixed ownership.

---

## MVI + Decompose Rules (STRICT)

- All new screen logic must use:
  - Decompose Component
  - MVIKotlin Store
- One component per feature responsibility.
- One store per feature component.
- A component may own multiple child components only when it is the clear parent boundary for that feature.
- Child components must use their own `ComponentContext` via `childContext(key)` or an official Decompose navigation model.
- Never pass one `ComponentContext` instance to multiple child components.

### Responsibilities

**Component**
- Lifecycle holder
- Store holder
- Navigation bridge only
- No business logic

**Store (Single Source of Truth)**
- Owns all screen state
- Handles intents → messages → state

**Reducer**
- Must be PURE
- Allowed:
  - state transformation only
- Forbidden:
  - IO
  - time access
  - randomness
  - platform calls
  - logging
  - navigation
  - mutation outside returned state

**Executor**
- Owns ALL side effects:
  - use cases
  - repository calls
  - time
  - ids
  - platform interactions
  - dispatcher switching
- Publishes:
  - messages (state updates)
  - labels (one-shot events)

**Labels**
- One-time events only
- Must NOT contain persistent state

---

## State & Flow Rules

- Store state must be exposed as `StateFlow`
- StateFlow must be:
  - immutable from outside
  - updated only via reducer

- One-off events MUST use Labels
- Do NOT:
  - use SharedFlow for UI events as state replacement
  - expose mutable flows across layers

- Avoid:
  - global flows
  - shared mutable streams
  - uncontrolled replay behavior

---

## Concurrency & Threading Rules

- Executor is responsible for dispatcher management
- Reducer must be thread-safe and pure

- Rules:
  - No blocking calls on Main thread
  - No dispatcher switching in UI or reducer
  - IO must happen on appropriate dispatcher
  - State updates must be deterministic

- Respect Java Memory Model:
  - no unsafe shared mutable state
  - no race conditions

---

## UI Rules (Compose)

- Compose is UI only
- Composables:
  - render state
  - emit intents

- Forbidden:
  - business logic
  - state ownership
  - side effects in composable body
  - coroutine launches without lifecycle awareness

- Do NOT:
  - calculate business state in UI
  - access repositories directly
  - mutate state

- Optimize:
  - avoid unnecessary recompositions
  - avoid heavy work on Main thread

---

## Map Rules (MapLibre)

- MapLibre is rendering only

- Rules:
  - No business logic in renderer
  - No state decisions inside map code
  - Map state must come from Store only
  - Renderer must be a pure projection of state

- Keep all MapLibre code Android-specific

---

## Data & Persistence

- Use Room only when required by current task
- Persistence must not leak into UI or reducer

- Repository:
  - hides data sources
  - used only via Executor

---

## Error Handling Rules

- Do NOT throw raw exceptions to UI

- Errors must be:
  - mapped to State (for persistent UI)
  - or Labels (for one-shot events)

- Follow existing project patterns
- If no pattern exists:
  - keep solution minimal
  - do not introduce global frameworks

---

## Feature Workflow Rules

- Always start from:
  1. Store (State / Intent / Label design)
  2. Executor logic
  3. Component wiring
  4. UI
  5. Renderer / platform adapters

- Component API must be intent-driven
- If a screen responsibility needs separate state, side effects, or lifecycle ownership, split it into a dedicated component + store pair.

- Before adding logic:
  decide if it belongs to:
  - reducer
  - executor
  - label handling
  - renderer

---

## Anti-Patterns (STRICTLY FORBIDDEN)

- Fake-MVI:
  - UI or component mutating state directly

- Reducer side effects:
  - IO, time, logging, navigation, platform calls

- UI business logic:
  - decision making beyond rendering

- Renderer business logic:
  - map interactions changing feature rules

- God classes:
  - mixing multiple responsibilities
  - growing central components, routers, or bridge classes that accumulate unrelated feature ownership

- Glue / bridge orchestration without ownership:
  - do not add helper, bridge, coordinator, or router classes that own cross-feature behavior without a clear bounded responsibility
  - if logic can belong to a feature component, store, or official navigation model, keep it there

- Base classes / inheritance abuse:
  - DO NOT create `Base*`
  - DO NOT create abstract hierarchies

- DI frameworks:
  - DO NOT introduce

- Scope violations:
  - modifying unrelated modules

- False completion reporting

---

## Background Work Rules

Use only if clearly justified:

- WorkManager
- Foreground Service
- Android Service

- No business logic in orchestration layer

---

## Formatting, Static Analysis, Tests

Use project tools if available:

- `./gradlew ktlintFormat`
- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew test`

Run ONLY relevant tasks.

---

## Verification Rules (STRICT)

- Never mark task as DONE without verification
- Never claim success without:
  - command
  - result

- Do not assume:
  - command existence
  - success

- If verification not possible:
  - explicitly state why

- If partial:
  - mark as PARTIALLY COMPLETE

---

## Definition of Done (DoD)

Task is DONE only if:

- Implementation matches ticket scope
- Store logic is correct and complete
- UI wired via intents only
- No business logic leaks (UI / renderer / component)
- Reducer is pure
- Executor handles all side effects
- Files changed are explainable
- Verification executed and results reported

If any condition fails → NOT DONE

---

## Task Execution Rules

- Make smallest change possible
- Do not expand scope
- Split oversized refactors into smaller tickets when they involve multiple feature responsibilities, unrelated package moves, or independent behavior changes.
- A ticket must stay bounded to one clear refactor goal that can be verified without relying on follow-up work.

- After completion:
  - update `docs/tasklist/<ticket>.md`

- Provide:
  - concise diff summary
  - commit message

- If extra files modified:
  - explain why

---

## Completion Status

- DONE
- PARTIALLY COMPLETE
- BLOCKED

Code written ≠ task completed

---

## Required Report Format

### Result
What was implemented

### Files Changed
List with purpose

### Verification
For each command:
- command
- result (PASS / FAIL / NOT RUN / NOT AVAILABLE)
- explanation

### Limitations
What is not verified

### Risks
Potential regressions

### Suggested Commit Message

Reports without verification are INVALID

---

## Errors and Secrets

- Never hardcode:
  - tokens
  - credentials
  - secrets

- Follow project logging patterns

---

## Output Preference

- Be concise
- Be precise
- No speculative improvements
- No workflow inventions unless requested
