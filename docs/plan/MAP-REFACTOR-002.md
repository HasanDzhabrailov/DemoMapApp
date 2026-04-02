# Plan — MAP-REFACTOR-002

## Summary
Изолировать Drawing в отдельный компонент со своим Store. Коммуникация через Output interface (explicit), не EventBus.

## Affected Modules
- `feature/map/impl/drawing/` — новый пакет
- Удаляем после миграции: `DrawingHandler.kt`, `CreatePointHandler.kt`

## File-Level Plan
- Create `DrawingComponent` interface with Output callback
- Create `DrawingModel` with drawing-specific fields only
- Create `DrawingStore` with State containing points/lines/polygons/drafts
- Executor calls use cases (CreatePoint, CreateLine, CreatePolygon)
- Reducer handles pure state transitions
- DefaultDrawingComponent wires store to output callback

## MVIKotlin Mapping
- Intent:
  - CreatePointClicked, CreateLineClicked, CreatePolygonClicked
  - PointLatitudeChanged, PointLongitudeChanged, PointTitleChanged
  - PointConfirmed, PointSheetDismissed
  - DrawingAddPositionClicked, DrawingRemoveLastPositionClicked
  - ShapeTitleChanged, ShapeConfirmed, ShapeSheetDismissed
  - CameraSnapshotReceived
- State:
  - `points: List<MapPoint>`
  - `lines: List<MapLine>`
  - `polygons: List<MapPolygon>`
  - `isCreatePointSheetVisible: Boolean`
  - `createPointDraft: CreatePointDraft?`
  - `drawingMode: DrawingMode?`
  - `shapeDrawingDraft: ShapeDrawingDraft?`
  - `isCreateShapeSheetVisible: Boolean`
- Reducer:
  - Sheet open/close
  - Draft field updates
  - Feature list updates (after creation)
- Executor:
  - CreateMapPointUseCase.invoke
  - CreateMapLineUseCase.invoke
  - CreateMapPolygonUseCase.invoke
  - TimeProvider.currentTimeMillis (side effect)
  - FeatureIdProvider.nextId (side effect)
- Label:
  - FeatureCreated(point/line/polygon)

## Migration Strategy
1. Create drawing package structure
2. Define DrawingComponent API with Output
3. Implement DrawingStore (State/Intent/Label)
4. Migrate DrawingHandler logic to Executor
5. Migrate CreatePointHandler logic to Executor
6. Implement Reducer with pure transitions
7. Wire DefaultDrawingComponent
8. Write tests

## Risks and Mitigations
- Risk: Logic duplication in use case calls
  - Mitigation: Keep use cases shared, only move invocation
- Risk: Draft initialization from camera snapshot
  - Mitigation: Store receives snapshot via intent

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- Unit test: CreatePoint flow (reducer + executor)
- Unit test: CreateLine flow
- Unit test: CreatePolygon flow
- Unit test: Output callback on FeatureCreated

## Task Breakdown
1. Create drawing package
2. Define DrawingComponent interface
3. Create DrawingStore structure
4. Implement Executor with use cases
5. Implement Reducer
6. Create Factory and DefaultDrawingComponent
7. Migrate handler logic
8. Write unit tests
9. Verify build and tests
