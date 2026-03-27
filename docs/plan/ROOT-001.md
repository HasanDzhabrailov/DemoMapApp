# Plan — ROOT-001

## Summary
Move map component instantiation to the root Decompose layer so the component is created by `RootComponent` / `DefaultRootComponent` and consumed downstream without local construction in UI.

## Affected Modules
- `root/api`: expose root-owned map child or creation contract if needed
- `root/impl`: create the map component instance here
- `root/ui`: consume root-owned child/component only if needed
- `feature/map/api`: no change expected unless constructor contract needs cleanup
- `feature/map/impl`: remove local creation path if it exists
- `composeApp/commonMain`: no Android-specific changes expected
- `composeApp/androidMain`: no change expected except existing map rendering wiring if needed
- `core/common`: no change expected
- `core/ui`: no change expected

## Implementation Notes
- Identify where the map component is currently instantiated
- Move instantiation to `DefaultRootComponent`
- Keep ownership in the root layer
- Pass the component instance down through Decompose composition
- Remove direct local creation from UI or non-root entry points
- Keep rendering and business logic unchanged
- Avoid broad refactoring

## Risks
- Existing UI may assume direct access to component construction
- Navigation/root child wiring may require a small contract adjustment
- The current creation path may be coupled to screen-local code

## Verification
- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew test`

## Task Breakdown
1. Identify the current map component creation path
2. Move component creation into `DefaultRootComponent`
3. Pass the root-owned instance through the required UI/root flow
4. Remove local creation outside root
5. Verify behavior remains unchanged
6. Run verification