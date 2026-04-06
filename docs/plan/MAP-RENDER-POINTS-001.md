# Plan — MAP-RENDER-POINTS-001

## Summary
Move points/lines/polygons rendering from central renderer to drawing feature.

## Affected Modules
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/PointStyleApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/LineStyleApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/PolygonStyleApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/StyleApplierConstants.android.kt` (clean)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/drawing/` (receive)

## Implementation Notes
1. Move applier files to drawing package
2. Update IDs to be drawing-scoped
3. Remove from central constants
4. Update MapRenderModelApplier to not call these
5. Drawing feature applies through Style directly

## Risks
- Risk 1: Feature click handling coordination
- Risk 2: Layer ordering with other features

## Verification
- `./gradlew :composeApp:compileDebugKotlin`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Move applier files to drawing
2. Update IDs
3. Clean constants file
4. Update model applier
5. Test rendering
