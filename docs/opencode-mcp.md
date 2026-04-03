# OpenCode MCP Docs Retrieval

This repository provides a repo-local OpenCode MCP server for official Android and Kotlin documentation.

## Config

- OpenCode project config lives in `opencode.json`.
- The MCP server is `official_docs`.
- It starts `python tools/opencode_official_docs_mcp.py`.
- The server only allows `developer.android.com` and `kotlinlang.org` pages.
- MCP timeout is set to `60000` ms to avoid Android search timeouts during focused page ranking.

## Retrieval Policy

- Always use search first.
- Then choose the single best page.
- Then fetch only the needed section with `official_docs_fetch`.
- Use `intent: api` for symbols, classes, methods, functions, packages, and annotations.
- Use `intent: guide` for workflows, architecture, lifecycle, setup, and conceptual questions.
- Do not fetch broad multi-page dumps.
- If the first result is weak, refine the query and search again instead of widening context.

## Available Tools

- `android_docs_search`
- `kotlin_docs_search`
- `official_docs_fetch`

## Suggested Build-Mode Usage

- Android API example:
  - Search: `android_docs_search` with `query: "androidx lifecycle ViewModel"`, `intent: "api"`
  - Fetch: `official_docs_fetch` for the selected reference page and only the needed section.
- Kotlin guide example:
  - Search: `kotlin_docs_search` with `query: "sequence builder"`, `intent: "guide"`
  - Fetch: `official_docs_fetch` for the selected page and only the needed section.

## Smoke Checks

Run these in an environment where `opencode` is installed.

Verified path in this workspace:

```bash
python tools/opencode_attach_run.py --agent build "Use official_docs. Search official Android docs for androidx lifecycle ViewModel with API preference, fetch only the needed section, and answer briefly."
python tools/opencode_attach_run.py --agent build "Use official_docs. Search official Kotlin docs for sequence builder with guide preference, fetch only the needed section, and answer briefly."
```

Note:

- In this environment, direct `opencode run ...` returned `Session not found`.
- `tools/opencode_attach_run.py` works around that by starting `opencode serve` and routing the request through `run --attach` automatically.

Expected outcome:

- The agent uses the `official_docs` MCP tools.
- It searches first.
- It fetches one focused page/section instead of dumping broad docs.
- Android answers cite `developer.android.com`.
- Kotlin answers cite `kotlinlang.org`.

## Environment Gap

The current workspace did not have the `opencode` CLI installed during initial implementation, so the repo-local config and local MCP server were verified directly first. After installing `opencode`, both Kotlin and Android smoke checks passed via the attach-based wrapper.
