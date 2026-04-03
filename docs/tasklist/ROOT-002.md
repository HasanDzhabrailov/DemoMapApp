# Tasklist — ROOT-002

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope: OpenCode / CLI MCP for official Android and Kotlin docs only
- [x] Confirm exact OpenCode / CLI MCP config format supported in the target environment
- [x] Confirm whether direct search/fetch is enough or minimal RAG is required
- [x] Identify whether repo-local config is sufficient or user-level setup is required
- [x] Add official Android documentation MCP wiring
- [x] Add official Kotlin documentation MCP wiring
- [x] Define retrieval policy: search -> exact page -> exact section -> answer
- [x] Ensure full-doc preload is disabled or avoided by default
- [x] Ensure broad documentation ingestion is avoided by default
- [x] Ensure retrieval fetches only the smallest useful section set
- [x] Ensure API-specific queries prefer API reference
- [x] Ensure conceptual queries prefer guide pages
- [x] If RAG is used, ensure chunk size and top-k stay minimal
- [x] Add minimal usage documentation for build mode
- [x] Run Android-docs smoke check in build mode
- [x] Run Kotlin-docs smoke check in build mode
- [x] Confirm retrieval returns focused context only
- [x] Confirm existing app code/build flow is unaffected
- [x] Update this tasklist
- [x] Prepare concise diff summary
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| OpenCode build mode can access official Android docs | DONE - verified via `opencode serve` + `opencode run --attach` |
| OpenCode build mode can access official Kotlin docs | DONE - verified via `opencode serve` + `opencode run --attach` |
| Retrieval is query-first and focused | DONE |
| Setup is documented from repository context | DONE |

## Notes

- Official OpenCode config format was confirmed from `https://opencode.ai/docs/config/` and `https://opencode.ai/docs/mcp-servers/`.
- Repo-local `opencode.json` is sufficient for MCP registration.
- Direct query-first search plus targeted fetch is sufficient; no RAG/index store was added.
- `opencode` was installed during verification.
- Direct `opencode run ...` returned `Session not found` in this environment.
- `opencode serve` plus `opencode run --attach ...` worked and was used as the verified smoke-check path.
- Kotlin smoke check passed with installed `opencode` and connected `official_docs` MCP.
- Android smoke check initially hit the MCP timeout limit at `20000` ms; config timeout was raised to `60000` ms and then verified.
- Post-implementation review fixed a critical Android search issue where unknown API queries could fall back to generic `reference/packages` results.
- Android API ranking now prefers pages that contain the requested symbol directly, which improves cases like `rememberUpdatedState`.
- Added a repo-local `tools/opencode_attach_run.py` wrapper to avoid the environment-specific `opencode run` -> `Session not found` failure.

## Suggested Commit Message

`chore: add OpenCode MCP and query-first doc retrieval for Android and Kotlin`
