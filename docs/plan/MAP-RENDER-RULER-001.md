# Plan — MAP-RENDER-RULER-001

## Summary
Move ruler rendering to ruler feature.

## Affected Modules
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/RulerStyleApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/ruler/` (receive)

## Implementation Notes
1. Move applier to ruler package
2. Update IDs to ruler-scoped
3. Remove from central constants
4. Ruler feature applies through Style

## Risks
- Risk 1: Layer ordering with drawing features

## Verification
- `./gradlew :composeApp:build`

## Task Breakdown
1. Move applier
2. Update IDs
3. Clean constants
4. Test rendering
