# Plan — MAP-REFACTOR-008

## Summary
UI разделение. Удаление монолитных composable с 50+ callback'ами.

## Affected Modules
- `feature/map/ui/` — полностью переписывается

## Deleted Files
```
feature/map/ui/MapScreenOverlays.kt         # DELETE (50+ parameters!)
```

## File-Level Plan
- Update `MapScreenContent.kt` — root UI, aggregates children
- Create component-specific UI files:
  - `DrawingContent.kt` — DrawingComponent only
  - `RulerOverlay.kt` — RulerComponent only
  - `LocationControls.kt` — LocationComponent only
  - `ViewportControls.kt` — ViewportComponent only
  - `ToolsOverlay.kt` — ToolsComponent only
- Each UI receives only its component (~5 intents, not 50+)

## UI Structure
```
MapScreenContent
├── MapRendererHost (aggregated MapState)
├── DrawingContent (DrawingComponent)
├── RulerOverlay (RulerComponent)
├── LocationControls (LocationComponent)
├── ViewportControls (ViewportComponent)
├── ToolsOverlay (ToolsComponent)
└── FeatureInfoWindow (if selected)
```

## New Tests
```
feature/map/impl/drawing/store/DrawingStoreTest.kt
feature/map/impl/ruler/store/RulerStoreTest.kt
feature/map/impl/location/store/LocationStoreTest.kt
feature/map/impl/viewport/store/ViewportStoreTest.kt
feature/map/impl/tools/store/ToolsStoreTest.kt
feature/map/impl/router/store/MapRouterStoreTest.kt
feature/map/impl/DefaultMapScreenComponentTest.kt
```

## Migration Strategy
1. Delete MapScreenOverlays
2. Update MapScreenContent
3. Create DrawingContent
4. Create RulerOverlay
5. Create LocationControls
6. Create ViewportControls
7. Create ToolsOverlay
8. Write unit tests
9. Write integration tests
10. Manual testing

## Risks and Mitigations
- Risk: UI behavior changes
  - Mitigation: Compare before/after screenshots/states
- Risk: Missing edge cases
  - Mitigation: Comprehensive manual testing

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- `./gradlew ktlintCheck`
- `./gradlew detekt`
- Manual: full user flow

## Task Breakdown
1. Delete old UI
2. Update MapScreenContent
3. Create Drawing UI
4. Create Ruler UI
5. Create Location UI
6. Create Viewport UI
7. Create Tools UI
8. Write unit tests
9. Write integration tests
10. Manual testing
11. Verify all checks pass
