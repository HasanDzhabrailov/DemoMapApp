# Plan — MAP-REFACTOR-001

## Summary
Создать Router Store для агрегации дочерних состояний. НЕ EventBus — explicit Output callbacks через parent. Router Store (а не Component) агрегирует MapState для renderer.

## Affected Modules
- `feature/map/impl/router/` — новый пакет

## File-Level Plan
- Create `MapRouterStore` interface with State/Intent/Label
- State contains nullable fields for each child component state
- Computed `mapState` property aggregates child states into MapState
- Router intents handle updates from child components via parent

## MVIKotlin Mapping
- Intent:
  - Drawing state updated
  - Ruler state updated
  - Location state updated
  - Viewport state updated
  - Tools state updated
- State:
  - `drawingState: DrawingStore.State?`
  - `rulerState: RulerStore.State?`
  - `locationState: LocationStore.State?`
  - `viewportState: ViewportStore.State?`
  - `toolsState: ToolsStore.State?`
  - Computed: `mapState: MapState`
- Reducer:
  - Update child state fields
  - Pure aggregation logic
- Executor:
  - Handle side effects if needed
  - Publish Labels for viewport commands
- Label:
  - Viewport command requested
  - Location request issued

## Migration Strategy
1. Create router package structure
2. Define Router Store interface
3. Implement State with aggregation logic
4. Create Factory, Executor, Reducer
5. Define intents for child state updates
6. Verify aggregation works correctly

## Risks and Mitigations
- Risk: Complex state aggregation logic
  - Mitigation: Keep aggregation pure and tested
- Risk: Circular dependencies between components
  - Mitigation: Explicit parent-child relationships only

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew ktlintCheck`
- Unit test: State aggregation produces correct MapState

## Task Breakdown
1. Create router package
2. Define MapRouterStore interface
3. Implement State with child state fields
4. Implement mapState aggregation
5. Create Factory, Executor, Reducer
6. Write unit tests
7. Verify build passes
