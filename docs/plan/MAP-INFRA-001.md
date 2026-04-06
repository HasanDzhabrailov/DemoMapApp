# Plan — MAP-INFRA-001

## Summary
Move base MapLibre infrastructure from feature.map.render to :map module.

## Affected Modules
- New: `:map`
- `composeApp/src/androidMain/kotlin/ru/tech/demomapapp/feature/map/render/` (move to :map)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/render/` (keep render model)

## Implementation Notes
1. Create :map module with KMP source sets
2. Move MapViewHolder, MapRenderer, lifecycle to :map
3. Keep feature-specific appliers in feature modules
4. Define clear interface for Style access
5. Feature modules depend on :map for infrastructure

## Risks
- Risk 1: Feature appliers need Style access
- Risk 2: KMP configuration for MapLibre

## Verification
- `./gradlew :map:build`
- `./gradlew :composeApp:build`

## Task Breakdown
1. Create :map module structure
2. Create build script with MapLibre dependency
3. Move infrastructure code to :map
4. Define Style access interface
5. Update feature dependencies
6. Verify build
