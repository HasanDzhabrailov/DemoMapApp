# Plan — ROOT-003

## Summary
Add a minimal repo-local OpenCode MCP server for MapLibre Native Android API documentation only, using query-first and section-focused retrieval.

## Affected Modules
- `repo root`: MCP config entrypoint
- `tools`: new MapLibre Android API MCP server
- `docs`: minimal setup and usage documentation
- `OpenCode / CLI local workflow`: smoke-check verification path

## Implementation Notes
- Add a separate MCP server such as `maplibre_android_docs`; do not fold this into `official_docs`.
- Restrict the source scope to `https://maplibre.org/maplibre-native/android/api/`.
- Expose only the minimal tools required for search and focused fetch.
- Keep retrieval order fixed as: search -> identify best page -> fetch exact section -> answer.
- Do not add Style Spec, Native book, examples, or GitHub sources.
- Prefer direct search plus targeted fetch over any indexing or RAG setup.
- Build a curated Android API catalog for ranking instead of broad site crawling.
- Prioritize packages and classes relevant to project usage:
  - `org.maplibre.android.maps`
  - `org.maplibre.android.style.sources`
  - `org.maplibre.android.style.layers`
  - `org.maplibre.android.style.expressions`
- Add symbol-aware ranking bonuses for common project queries:
  - `MapView`
  - `Style`
  - `GeoJsonSource`
  - `SymbolLayer`
  - `LineLayer`
  - `FillLayer`
  - `CircleLayer`
- Prefer class pages when the query is symbol-specific.
- Fall back to package pages when exact symbol pages are not confidently available.
- Fetch only the smallest useful section by heading, fragment, or symbol hint.
- Document that this MCP is optimized for Android API questions around `Style -> Sources -> Layers`.
- Reuse the existing `tools/opencode_attach_run.py` smoke-check path if direct `opencode run` remains unreliable in the environment.

## Risks
- Risk 1: Dokka-generated URLs may not be easy to predict for every class page.
- Risk 2: Package pages may sometimes outrank class pages without stronger symbol-aware scoring.
- Risk 3: Search latency may require timeout tuning similar to the existing official-docs server.
- Risk 4: Some detailed usage questions may really belong to Style Spec, but that source is intentionally excluded from scope.

## Verification
- Run a smoke prompt for `MapView` Android API retrieval.
- Run a smoke prompt for `Style.addSource` or `Style.addLayer`.
- Run a smoke prompt for `GeoJsonSource`.
- Run a smoke prompt for `SymbolLayer`.
- Confirm the retrieved URLs stay under the MapLibre Android API path only.
- Confirm retrieval stays focused to one selected page and one needed section.
- Confirm no Style Spec or other MapLibre sources are used.

## Task Breakdown
1. Confirm the exact root-scoped ticket and scope for MapLibre Android API only.
2. Inspect the existing `official_docs` MCP pattern and reuse only the minimal architecture needed.
3. Add a new repo-local MCP server for MapLibre Android API.
4. Implement restricted search for `maplibre-native/android/api`.
5. Implement focused section fetch for selected Android API pages.
6. Add symbol-aware ranking for `MapView`, `Style`, `GeoJsonSource`, `SymbolLayer`, and related layer/source APIs.
7. Add repo-local OpenCode config entry for the new MCP server.
8. Document build-mode usage and retrieval policy.
9. Run build-mode smoke checks against MapLibre Android API queries.
10. Record exact verification results and any unavoidable environment-specific limitations.
