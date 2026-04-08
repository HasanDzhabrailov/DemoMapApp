# OpenCode MCP Docs Retrieval

This repository provides repo-local OpenCode MCP servers for official Android/Kotlin documentation and for MapLibre Android API documentation.

## Config

- OpenCode project config lives in `opencode.json`.
- `official_docs` starts `python tools/opencode_official_docs_mcp.py`.
- `maplibre_android_docs` starts `python tools/opencode_maplibre_android_docs_mcp.py`.
- `official_docs` only allows `developer.android.com` and `kotlinlang.org` pages.
- `maplibre_android_docs` only allows `https://maplibre.org/maplibre-native/android/api/` pages.
- MCP timeout is set to `60000` ms to avoid focused-ranking timeouts.

## Retrieval Policy

- Always use search first.
- Then choose the single best page.
- Then fetch only the needed section with the matching fetch tool.
- Use `intent: api` for symbols, classes, methods, functions, packages, and annotations.
- Use `intent: guide` for workflows, architecture, lifecycle, setup, and conceptual questions.
- Do not fetch broad multi-page dumps.
- If the first result is weak, refine the query and search again instead of widening context.
- For MapLibre Android API, keep retrieval inside the Android API site only and avoid Style Spec, Native book, examples, GitHub, and community sources.

## Known Limitations

- In this workspace, `opencode` may be unavailable in `PATH` even when the local binary exists under `~/.opencode/bin`.
- Use `tools/opencode_attach_run.py` for smoke checks and local validation when direct `opencode run ...` is unavailable or returns session-attach issues.
- MapLibre Android API retrieval infrastructure is restricted correctly, but answer quality can still vary at the agent layer.
- With broad prompts, the agent may occasionally add extra commentary or cite a non-API MapLibre page even though the MCP search/fetch stayed within the Android API docs.
- For MapLibre questions, prefer prompts that explicitly say `Use maplibre_android_docs only` and `cite only a maplibre.org/maplibre-native/android/api URL`.
- `official_docs_fetch` for Android pages can be sensitive to vague `section_hint` values; if the first fetch is too generic, retry with a more specific symbol or heading hint.

## Available Tools

- `android_docs_search`
- `kotlin_docs_search`
- `official_docs_fetch`
- `maplibre_android_docs_search`
- `maplibre_android_docs_fetch`

## Suggested Build-Mode Usage

- Android API example:
- Search: `android_docs_search` with `query: "androidx lifecycle ViewModel"`, `intent: "api"`
- Fetch: `official_docs_fetch` for the selected reference page and only the needed section.
- Kotlin guide example:
- Search: `kotlin_docs_search` with `query: "sequence builder"`, `intent: "guide"`
- Fetch: `official_docs_fetch` for the selected page and only the needed section.
- MapLibre Android API example:
- Search: `maplibre_android_docs_search` with `query: "Style addSource"`
- Fetch: `maplibre_android_docs_fetch` for the selected page and only the needed section.

## Smoke Checks

Run these in an environment where `opencode` is installed.

Verified path in this workspace:

```bash
python tools/opencode_attach_run.py --agent build "Use official_docs. Search official Android docs for androidx lifecycle ViewModel with API preference, fetch only the needed section, and answer briefly."
python tools/opencode_attach_run.py --agent build "Use official_docs. Search official Kotlin docs for sequence builder with guide preference, fetch only the needed section, and answer briefly."
python tools/opencode_attach_run.py --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for MapView, fetch only the needed section, and answer briefly."
python tools/opencode_attach_run.py --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for Style.addSource, fetch only the needed section, and answer briefly."
python tools/opencode_attach_run.py --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for GeoJsonSource, fetch only the needed section, and answer briefly."
python tools/opencode_attach_run.py --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for SymbolLayer, fetch only the needed section, and answer briefly."
```

Note:

- In this environment, direct `opencode run ...` returned `Session not found`.
- `tools/opencode_attach_run.py` works around that by starting `opencode serve` and routing the request through `run --attach` automatically.
- For an interactive OpenCode shell that keeps the same local MCP servers alive, use `python tools/opencode_attach_run.py --shell`.
- If you need the local OpenCode server to outlive a single client session, use `python tools/opencode_attach_run.py --serve-only`, then attach explicitly with the printed `opencode attach ...` command.

Interactive shell example:

```bash
python tools/opencode_attach_run.py --shell
```

Persistent server example:

```bash
python tools/opencode_attach_run.py --serve-only
```

Then attach from another terminal using the printed command, for example:

```bash
opencode attach http://127.0.0.1:4096 --dir C:/Users/Maestro/AndroidStudioProjects/DemoMapApp
```

Expected outcome:

- The agent uses the `official_docs` MCP tools.
- It searches first.
- It fetches one focused page/section instead of dumping broad docs.
- Android answers cite `developer.android.com`.
- Kotlin answers cite `kotlinlang.org`.
- MapLibre Android answers cite `maplibre.org/maplibre-native/android/api/` only.

Practical note:

- The expected source restriction is reliably enforced at the MCP layer, but stricter prompting may still be needed to keep the final natural-language answer from mentioning adjacent non-API MapLibre material.

## Environment Gap

The current workspace did not have the `opencode` CLI installed during initial implementation, so the repo-local config and local MCP server were verified directly first. After installing `opencode`, both Kotlin and Android smoke checks passed via the attach-based wrapper.
