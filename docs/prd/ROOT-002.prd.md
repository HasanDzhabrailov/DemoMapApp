# ROOT-002 — Add OpenCode MCP and query-first retrieval for official Android and Kotlin documentation

## Context
The repository currently has no MCP configuration for documentation access.

For build-mode execution, the assistant should be able to use official Android and Kotlin documentation instead of relying only on local code context.

Documentation access must stay selective. The setup should retrieve only the sections needed for the current question instead of loading broad documentation context.

This is a root/tooling concern because it affects repository-level developer workflow rather than any business feature module.

## Goal
Add repository-owned MCP configuration for OpenCode / CLI so build mode can access official Android and Kotlin documentation through query-first, token-efficient retrieval.

## Non-Goals
- No application business logic changes
- No map feature refactor
- No Android runtime behavior changes
- No replacement of existing project architecture rules
- No unrelated tooling cleanup
- No broad knowledge-base ingestion beyond official Android and Kotlin docs
- No full-site or full-manual ingestion by default
- No third documentation source unless required for Android or Kotlin official docs
- No global machine-specific setup that cannot be expressed or documented from the repository

## User Scenarios
- Scenario 1: A developer runs OpenCode in build mode and the assistant can consult official Android documentation for a specific API or behavior.
- Scenario 2: A developer runs OpenCode in build mode and the assistant can consult official Kotlin documentation for a specific language feature or stdlib API.
- Scenario 3: The assistant retrieves only the relevant documentation fragments instead of large unfiltered dumps.
- Scenario 4: A developer can understand from repository files how this MCP and retrieval setup is expected to work.
- Scenario 5: Existing app code and build behavior remain unchanged.

## Retrieval Policy
- Never preload full Android or Kotlin documentation into context by default.
- Always use query-first retrieval.
- Retrieval order must be: search -> choose the most relevant page -> fetch only the needed section -> answer.
- Fetch at most the minimum number of sections required for the current request.
- Prefer section-level retrieval over page-level retrieval when supported.
- Prefer API reference pages when the request is API-specific.
- Prefer guide pages when the request is workflow- or concept-specific.
- Do not retrieve adjacent sections unless they are required to answer correctly.
- Do not use broad site-wide dumps when a narrower source can answer the request.
- If RAG is used, chunking must be small and retrieval must return only top relevant chunks.
- If confidence is low after the first retrieval, refine the query instead of expanding context aggressively.

## Acceptance Criteria
- [ ] Repository contains OpenCode / CLI MCP configuration for official Android documentation access.
- [ ] Repository contains OpenCode / CLI MCP configuration for official Kotlin documentation access.
- [ ] Configuration is scoped to official sources only.
- [ ] Retrieval follows a query-first flow: search first, then fetch only the relevant page or section.
- [ ] Full-document or broad multi-page ingestion is not used by default.
- [ ] Retrieval fetches no more than the smallest useful set of sections for a query.
- [ ] API-specific queries prefer API reference sources.
- [ ] Conceptual or workflow queries prefer guide/documentation sources.
- [ ] If RAG is required, it returns only top relevant chunks and does not inject large context by default.
- [ ] Repository includes minimal usage documentation for how build mode should use this setup.
- [ ] No app feature code is modified unless minimally required for tooling wiring.
- [ ] A build-mode smoke check is defined and passes in the target environment.

## Constraints
- Follow `AGENTS.md`
- Keep the change minimal and root-scoped
- Prefer repository-local configuration over undocumented machine-only setup
- Do not introduce secrets or hardcoded credentials
- Do not expand scope into general AI tooling redesign
- Prefer query-time retrieval over bulk indexing
- If RAG is required, use minimal indexing and retrieval scope
- Do not fetch or inject large documentation bodies when a narrower section-level fetch is possible
- If OpenCode requires user-level configuration outside the repo, document the exact gap instead of hiding it

## Success Metrics
- Build mode can resolve Android documentation through official sources
- Build mode can resolve Kotlin documentation through official sources
- Retrieval returns focused results for the current query only
- Setup is reproducible by another developer from repository context plus documented prerequisites
- Existing project build/test workflow is unaffected

## Open Questions
- What exact OpenCode / CLI config filename and schema should be used for repo-local MCP registration?
- Can the target OpenCode build mode consume repo-local MCP config directly, or does it require user-level registration outside the repository?
- Is direct MCP search/fetch sufficient, or is minimal section-level RAG required?
- What is the exact smoke-check command used in your local OpenCode build workflow?
