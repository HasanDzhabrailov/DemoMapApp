# Plan — MAP-REFACTOR-007

## Summary
Router Component. Удаление монолитного MapStore. Компонент = только lifecycle holder + navigation bridge (no business logic).

## Affected Modules
- `feature/map/impl/` — переписывается
- `feature/map/api/` — обновляется
- `feature/map/ui/` — удаляется MapScreenOverlays

## Deleted Files
```
feature/map/impl/store/                    # DELETE всё
├── MapStore.kt
├── MapStoreExecutor.kt
├── MapStoreReducer.kt
├── MapStoreMessage.kt
├── MapStoreFactory.kt
├── MapStoreHolder.kt
└── handler/                                # DELETE
    ├── CreatePointHandler.kt
    ├── DrawingHandler.kt
    ├── LocationHandler.kt
    └── FeatureClickHandler.kt

feature/map/api/MapScreenComponent.kt       # REPLACE
feature/map/impl/DefaultMapScreenComponent.kt # REPLACE
feature/map/ui/MapScreenOverlays.kt         # DELETE
feature/map/commonTest/MapStore*Test.kt     # DELETE (rewrite)
```

## File-Level Plan
- Create new `MapScreenComponent` interface (facade)
- Create new `MapScreenModel` aggregating child models
- Create `DefaultMapScreenComponent` as Router
- Wire all child components (Drawing, Ruler, Location, Viewport, Tools)
- Connect child Outputs to RouterStore
- Aggregate model from RouterStore (not Component)

## MVIKotlin Mapping (Router Level)
- Router Store State:
  - Contains all child states
  - Computes aggregated MapState
- Router Intents:
  - Child state updates (from Output callbacks)
- Router Labels:
  - ViewportCommand (to parent)
  - LocationRequest (to parent)

## Migration Strategy
1. Delete old MapStore files
2. Create new MapScreenComponent API
3. Implement Router component
4. Wire all children with Output callbacks
5. Connect Outputs to RouterStore
6. Verify integration

## Risks and Mitigations
- Risk: Broken integration between components
  - Mitigation: Comprehensive integration tests
- Risk: Missing functionality from old MapStore
  - Mitigation: Feature parity checklist

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- `./gradlew ktlintCheck`
- `./gradlew detekt`
- Integration test: Full point creation flow
- Integration test: Ruler with location
- Integration test: Layer management

## Task Breakdown
1. Delete old MapStore files
2. Create new MapScreenComponent API
3. Create MapScreenModel
4. Implement Router component
5. Wire DrawingComponent
6. Wire RulerComponent
7. Wire LocationComponent
8. Wire ViewportComponent
9. Wire ToolsComponent
10. Write integration tests
11. Verify build and tests
