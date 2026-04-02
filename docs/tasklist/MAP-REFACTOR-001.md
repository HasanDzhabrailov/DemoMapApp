# Tasklist - MAP-REFACTOR-001

## Completed
- [x] Read PRD
- [x] Read plan
- [x] Confirm scope
- [x] Create `feature/map/impl/router/` package structure
- [x] Create `MapRouterStore` interface with State/Intent/Label
- [x] Create `MapRouterStore.State` with fields for all child states
- [x] Implement `mapState` computed property in State (pure aggregation)
- [x] Create `MapRouterStoreFactory` for store creation
- [x] Create `MapRouterExecutor` for side effects
- [x] Create `MapRouterReducer` (pure, no side effects)
- [x] Define Router intents for child state updates

## Verification Results
| Check | Status |
|-------|--------|
| Router Store structure created | DONE |
| State aggregation works | DONE |
| No EventBus/global flows | DONE |
| Reducer is pure | DONE |
| Build passes (Android) | PASS |
| Build passes (JVM) | PASS |

## Notes
- ktlint errors found in existing androidMain files (pre-existing, not related to this change)
- Router Store successfully aggregates 8 child states via computed properties
- All MVI rules followed: pure reducer, explicit intents, labels for one-shot events

## Suggested Commit Message
```
feat(map): create Router Store for state aggregation (MAP-REFACTOR-001)

- Add MapRouterStore interface with State/Intent/Label
- Implement State with nullable child state fields
- Add computed mapState property (pure aggregation)
- Create Executor for handling side effects
- Create pure Reducer for state transformations
- Add Factory for store creation

Router Store aggregates child component states without global flows,
following MVIKotlin patterns with explicit Output callbacks.
```
