# Plan — MAP-RENDER-DRAWING-001

## Summary
Move drawing preview rendering to drawing feature.

## Affected Modules
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/DrawingPreviewApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/drawing/` (receive)

## Implementation Notes
1. Move applier to drawing package
2. Update IDs to drawing-scoped
3. Remove from central constants
4. Drawing feature applies through Style

## Verification
- `./gradlew :composeApp:build`

## Task Breakdown
1. Move applier
2. Update IDs
3. Clean constants
4. Test rendering
