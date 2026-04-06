# Plan — MAP-RENDER-RASTER-001

## Summary
Move raster tile overlay rendering to tools feature.

## Affected Modules
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/RasterTileStyleApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/tools/` (receive)

## Implementation Notes
1. Move applier to tools package
2. Update IDs to tools-scoped
3. Remove from central constants
4. Tools feature applies through Style
5. Preserve layer insertion order

## Risks
- Risk 1: Overlay layer ordering

## Verification
- `./gradlew :composeApp:build`

## Task Breakdown
1. Move applier
2. Update IDs
3. Clean constants
4. Test overlay rendering
