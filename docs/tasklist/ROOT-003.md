# Tasklist — ROOT-003

- [ ] Read PRD
- [ ] Read plan
- [ ] Confirm scope: MapLibre Android API only
- [ ] Confirm non-goals: no Style Spec, no Native book, no examples, no GitHub
- [ ] Reuse existing MCP architecture only where minimal
- [ ] Add new repo-local MCP server for MapLibre Android API
- [ ] Restrict allowlist to `maplibre.org/maplibre-native/android/api`
- [ ] Add `maplibre_android_docs_search`
- [ ] Add `maplibre_android_docs_fetch`
- [ ] Implement query-first retrieval: search -> exact page -> exact section -> answer
- [ ] Ensure broad ingestion is avoided by default
- [ ] Ensure retrieval fetches only the smallest useful section set
- [ ] Add package-aware ranking for `maps`, `style.sources`, `style.layers`, `style.expressions`
- [ ] Add symbol-aware ranking for `MapView`
- [ ] Add symbol-aware ranking for `Style`
- [ ] Add symbol-aware ranking for `GeoJsonSource`
- [ ] Add symbol-aware ranking for `SymbolLayer`
- [ ] Add symbol-aware ranking for common layer/source classes used by the project
- [ ] Prefer class pages over package pages when confidence is high
- [ ] Add fallback to package pages when exact symbol page is weak or unavailable
- [ ] Register the new MCP in `opencode.json`
- [ ] Document minimal build-mode usage
- [ ] Document retrieval policy and source restrictions
- [ ] Update `AGENTS.md` with the new MapLibre MCP usage rule
- [ ] Run MapView smoke check
- [ ] Run Style smoke check
- [ ] Run GeoJsonSource smoke check
- [ ] Run SymbolLayer smoke check
- [ ] Confirm retrieval remains inside MapLibre Android API URLs only
- [ ] Confirm retrieval stays focused and token-efficient
- [ ] Confirm existing app code/build flow is unaffected
- [ ] Update this tasklist with verification notes
- [ ] Prepare concise diff summary
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| OpenCode build mode can access MapLibre Android API docs | PENDING |
| Retrieval is query-first and focused | PENDING |
| `GeoJsonSource` lookup resolves correctly | PENDING |
| `SymbolLayer` lookup resolves correctly | PENDING |
| Setup is documented from repository context | PENDING |

## Suggested Commit Message
`chore: add OpenCode MCP for MapLibre Android API`
