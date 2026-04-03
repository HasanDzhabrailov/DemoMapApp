# Plan — MAP-ARCH-011

## Summary
Reduce `DefaultMapScreenComponent` to renderer and map-specific behavior only.

## Affected Modules
- `feature/map/mapscreen`
- `feature/map/host`

## Implementation Notes
- Keep map rendering and camera-facing responsibilities in mapscreen.
- Move non-map orchestration to host.

## Risks
- Risk 1: Responsibility boundaries may still be fuzzy for selection or viewport commands.

## Verification
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`

## Task Breakdown
1. Identify map-only responsibilities.
2. Move non-map responsibilities to host.
3. Update wiring.
4. Compile and test.
