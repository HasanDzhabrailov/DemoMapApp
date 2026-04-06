# Tasklist — ROOT-003

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope: MapLibre Android API only
- [x] Confirm non-goals: no Style Spec, no Native book, no examples, no GitHub
- [x] Reuse existing MCP architecture only where minimal
- [x] Add new repo-local MCP server for MapLibre Android API
- [x] Restrict allowlist to `maplibre.org/maplibre-native/android/api`
- [x] Add `maplibre_android_docs_search`
- [x] Add `maplibre_android_docs_fetch`
- [x] Implement query-first retrieval: search -> exact page -> exact section -> answer
- [x] Ensure broad ingestion is avoided by default
- [x] Ensure retrieval fetches only the smallest useful section set
- [x] Add package-aware ranking for `maps`, `style.sources`, `style.layers`, `style.expressions`
- [x] Add symbol-aware ranking for `MapView`
- [x] Add symbol-aware ranking for `Style`
- [x] Add symbol-aware ranking for `GeoJsonSource`
- [x] Add symbol-aware ranking for `SymbolLayer`
- [x] Add symbol-aware ranking for common layer/source classes used by the project
- [x] Prefer class pages over package pages when confidence is high
- [x] Add fallback to package pages when exact symbol page is weak or unavailable
- [x] Register the new MCP in `opencode.json`
- [x] Document minimal build-mode usage
- [x] Document retrieval policy and source restrictions
- [x] Update `AGENTS.md` with the new MapLibre MCP usage rule
- [x] Run MapView smoke check
- [x] Run Style smoke check
- [x] Run GeoJsonSource smoke check
- [x] Run SymbolLayer smoke check
- [x] Confirm retrieval remains inside MapLibre Android API URLs only
- [x] Confirm retrieval stays focused and token-efficient
- [x] Confirm existing app code/build flow is unaffected
- [x] Update this tasklist with verification notes
- [x] Prepare concise diff summary
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| OpenCode build mode can access MapLibre Android API docs | DONE |
| Retrieval is query-first and focused | DONE |
| `GeoJsonSource` lookup resolves correctly | DONE |
| `SymbolLayer` lookup resolves correctly | DONE |
| Setup is documented from repository context | DONE |

## Verification Notes

- `python -m py_compile "tools/opencode_maplibre_android_docs_mcp.py" "tools/opencode_official_docs_mcp.py" "tools/opencode_attach_run.py"` -> passed
- `python -c "import json; json.load(open('opencode.json', 'r', encoding='utf-8')); print('opencode.json OK')"` -> passed
- Direct MCP search checks passed for `MapView`, `Style.addSource`, `GeoJsonSource`, `SymbolLayer`
- Direct MCP fetch check passed for `Style.addSource` and returned focused content from MapLibre Android API page
- Direct MCP allowlist check rejected `https://developer.android.com/reference/androidx/lifecycle/ViewModel`
- `python "tools/opencode_attach_run.py" --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for MapView, fetch only the needed section, and answer briefly."` -> passed
- `python "tools/opencode_attach_run.py" --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for Style.addSource, fetch only the needed section, and answer briefly."` -> passed
- `python "tools/opencode_attach_run.py" --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for GeoJsonSource, fetch only the needed section, and answer briefly."` -> passed
- `python "tools/opencode_attach_run.py" --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for SymbolLayer, fetch only the needed section, and answer briefly."` -> passed
- `./gradlew.bat help` -> `BUILD SUCCESSFUL`
- `python -m py_compile "tools/opencode_maplibre_android_docs_mcp.py" "tools/maplibre_mcp_smoke_check.py"` -> passed
- `python "tools/maplibre_mcp_smoke_check.py"` -> passed
- Direct MCP search for `Style.addSource` now ranks `add-source.html` above `Style/index.html` with a larger score gap
- Direct MCP fetch for `Style.addSource` now returns a tighter section with signature + summary instead of Dokka page chrome
- `python "tools/opencode_attach_run.py" --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for Style.addSource, fetch only the needed section, and answer briefly with the selected URL."` -> passed and selected exact member page
- Direct MCP fetch from `Style/index.html` with `section_hint: addLayerAbove` now auto-resolves to `add-layer-above.html`
- `python "tools/opencode_attach_run.py" --agent build "Use maplibre_android_docs. Search MapLibre Android API docs for Style.addLayerAbove, fetch only the needed section, and answer briefly with the selected URL."` -> passed and selected exact member page

## Diff Summary

- Added a dedicated repo-local MCP server for MapLibre Android API search and focused fetch
- Registered the new MCP in `opencode.json`
- Documented usage and retrieval policy in `docs/opencode-mcp.md`
- Added MapLibre MCP usage rules to `AGENTS.md`
- Improved exact member-page ranking over class/package pages for method queries
- Tightened Dokka HTML extraction for cleaner focused fetch output
- Added repeatable smoke checks in `tools/maplibre_mcp_smoke_check.py`
- Added class-page fetch auto-resolution to exact member pages when `section_hint` names a method/property

## Suggested Commit Message
`chore: refine MapLibre MCP ranking and fetch quality`
