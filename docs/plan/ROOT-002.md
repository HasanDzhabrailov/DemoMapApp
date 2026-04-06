# Plan — ROOT-002

## Summary
Add minimal root-level OpenCode / CLI MCP configuration so build mode can access official Android and Kotlin documentation through query-first retrieval.

## Affected Modules
- `repo root`: MCP config entrypoint if supported by OpenCode
- `docs`: minimal setup and usage documentation
- `OpenCode / CLI local workflow`: smoke-check verification path

## Implementation Notes
- First confirm the exact OpenCode / CLI MCP config format supported in your environment.
- Register only official Android and Kotlin documentation sources.
- Prefer direct search plus targeted fetch over broad ingestion.
- Retrieval order should be fixed as: search -> identify best source -> fetch exact section -> answer.
- Never configure default bulk ingestion of Android or Kotlin docs.
- Prefer source filtering before content retrieval.
- Prefer exact-page or exact-section fetch over multi-page fetch.
- Prefer API reference for symbol, class, function, annotation, or package queries.
- Prefer guides for lifecycle, architecture, workflow, migration, and conceptual questions.
- If RAG is required, keep indexing scope minimal and retrieval constrained to the smallest useful chunk.
- If RAG is required, keep chunk size small, top-k low, and source scope restricted to official Android and Kotlin docs only.
- If the first result is insufficient, retry with a refined query before widening retrieval.
- Avoid returning large documentation excerpts by default.
- Keep configuration repository-owned where possible.
- If user-level bootstrap is unavoidable, document the exact manual step and keep repo changes minimal.
- Do not touch app feature code unless the tooling entrypoint is stored in-repo.
- Add a short usage note describing how build mode should invoke or consume the MCP-enabled setup.

## Risks
- Risk 1: OpenCode may require machine-level MCP registration that cannot be fully expressed in-repo.
- Risk 2: Official documentation endpoints may need a specific MCP server adapter rather than direct URL registration.
- Risk 3: A naive indexing setup may pull too much irrelevant context and waste tokens.
- Risk 4: The local build-mode command may differ from the repository expectation, making verification environment-specific.

## Verification
- Run the existing OpenCode build-mode command with a smoke prompt that requires Android official docs access.
- Run the existing OpenCode build-mode command with a smoke prompt that requires Kotlin official docs access.
- Confirm retrieval uses targeted lookup and does not return broad documentation dumps.
- Confirm API-specific prompts prefer API reference sources.
- Confirm conceptual prompts prefer guide/documentation sources.
- Confirm repository documentation matches the actual setup path used locally.

## Task Breakdown
1. Confirm the OpenCode / CLI MCP configuration mechanism available in the target environment.
2. Decide whether direct search/fetch is sufficient or minimal section-level RAG is required.
3. Add minimal configuration for official Android documentation access.
4. Add minimal configuration for official Kotlin documentation access.
5. Configure or document query-first retrieval behavior.
6. Ensure broad documentation ingestion is disabled or avoided by default.
7. Add concise repository documentation for build-mode usage and prerequisites.
8. Run build-mode smoke checks against both documentation sources.
9. Record exact verification results and any unavoidable local-only prerequisites.
