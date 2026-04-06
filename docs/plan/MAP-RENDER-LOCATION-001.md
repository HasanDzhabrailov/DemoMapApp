# Plan — MAP-RENDER-LOCATION-001

## Summary
Move current location rendering to location feature.

## Affected Modules
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/CurrentLocationStyleApplier.android.kt` (move)
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/location/` (receive)

## Implementation Notes
1. Move applier to location package
2. Update IDs to location-scoped
3. Remove from central constants
4. Location feature applies through Style

## Risks
- Risk 1: Layer ordering with user features

## Verification
- `./gradlew :composeApp:build`

## Task Breakdown
1. Move applier
2. Update IDs
3. Clean constants
4. Test rendering
