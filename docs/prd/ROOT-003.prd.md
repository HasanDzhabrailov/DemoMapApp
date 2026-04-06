# ROOT-003 — Add OpenCode MCP for MapLibre Android API with query-first retrieval

## Context
The repository already provides a repo-local OpenCode MCP setup for official Android and Kotlin documentation.

The project also depends on MapLibre Android APIs, especially around `MapView`, `Style`, `Source`, and `Layer` usage. Build-mode execution should be able to consult MapLibre Android API documentation directly without broadening retrieval scope to unrelated MapLibre sources.

Documentation access must stay selective and token-efficient. The setup should retrieve only the Android API page and section needed for the current question.

## Goal
Add repository-owned MCP configuration so OpenCode build mode can access MapLibre Native Android API documentation through query-first, section-focused retrieval.

## Non-Goals
- No application business logic changes
- No map feature refactor
- No Android runtime behavior changes
- No changes to existing `official_docs` behavior unless minimally required for coexistence
- No MapLibre Style Spec integration
- No MapLibre Native book integration
- No GitHub, examples, issues, or community sources
- No broad website crawling or bulk ingestion
- No RAG/index store unless direct search/fetch proves insufficient

## User Scenarios
- Scenario 1: A developer asks about `MapView` behavior and the assistant uses MapLibre Android API docs.
- Scenario 2: A developer asks about `Style.addSource`, `Style.addLayer`, or layer ordering and the assistant uses MapLibre Android API docs.
- Scenario 3: A developer asks about `GeoJsonSource` usage and the assistant retrieves the relevant Android API page and section only.
- Scenario 4: A developer asks about `SymbolLayer` properties and the assistant retrieves the relevant Android API page and section only.
- Scenario 5: The assistant avoids spending tokens on unrelated MapLibre docs such as Style Spec or developer book pages.

## Retrieval Policy
- Never preload broad MapLibre documentation into context by default.
- Always use query-first retrieval.
- Retrieval order must be: search -> choose the most relevant page -> fetch only the needed section -> answer.
- Fetch at most the minimum number of sections required for the current request.
- Restrict sources to `https://maplibre.org/maplibre-native/android/api/`.
- Prefer class or symbol pages over package pages when the request is symbol-specific.
- Prefer package pages only when no precise class page is available or when the query is package-level.
- Do not fetch Style Spec, Native book, examples, GitHub pages, or adjacent sections unless required.
- If confidence is low after the first retrieval, refine the query instead of expanding context aggressively.

## Acceptance Criteria
- [ ] Repository contains OpenCode / CLI MCP configuration for MapLibre Android API access.
- [ ] Retrieval is restricted to `maplibre.org/maplibre-native/android/api/`.
- [ ] Retrieval follows a query-first flow: search first, then fetch only the relevant page or section.
- [ ] Full-document or broad multi-page ingestion is not used by default.
- [ ] Retrieval fetches no more than the smallest useful section set.
- [ ] Symbol-specific queries prefer precise API pages over package pages.
- [ ] `GeoJsonSource` queries are handled correctly.
- [ ] `SymbolLayer` queries are handled correctly.
- [ ] Style-related Android API queries such as `Style`, `Source`, and `Layer` are ranked well.
- [ ] Repository includes minimal usage documentation for how build mode should use this setup.
- [ ] No app feature code is modified unless minimally required for tooling wiring.
- [ ] A build-mode smoke check is defined and passes in the target environment.

## Constraints
- Follow `AGENTS.md`
- Keep the change minimal and root-scoped
- Prefer repository-local configuration
- Do not introduce secrets or hardcoded credentials
- Do not expand scope into non-Android MapLibre documentation
- Prefer direct search plus targeted fetch over RAG or indexing
- If user-level bootstrap is unavoidable, document the exact gap instead of hiding it

## Success Metrics
- Build mode can resolve MapLibre Android API documentation through repo-owned MCP setup
- Retrieval returns focused results for the current query only
- `GeoJsonSource` and `SymbolLayer` lookups resolve reliably
- Existing project build/test workflow is unaffected

## Open Questions
- Is one search tool enough, or should search be split by package/class heuristics internally?
- Are Dokka page URLs stable enough for direct class-page candidate generation?
- What timeout is needed for reliable ranking against the MapLibre Android API site?
