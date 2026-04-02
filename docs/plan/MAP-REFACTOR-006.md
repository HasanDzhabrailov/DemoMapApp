# Plan — MAP-REFACTOR-006

## Summary
Изолировать Tools (меню, слои, доступные карты). Выносим `MapLayerManagementReducer`.

## Affected Modules
- `feature/map/impl/tools/` — новый пакет
- Удаляем после миграции: `MapLayerManagementReducer.kt`

## File-Level Plan
- Create `ToolsComponent` interface with Output callback
- Create `ToolsModel` with menu state, catalog, layers
- Create `ToolsStore` with State/Intent/Label
- Migrate layer management logic from MapLayerManagementReducer
- Reducer handles all layer operations (pure)

## MVIKotlin Mapping
- Intent:
  - MapToolsClicked, MapToolsDismissed
  - AvailableMapsClicked, AvailableMapsDismissed
  - AvailableMapSelected(mapId), AvailableMapConfirmed, AvailableMapSelectionDismissed
  - MapsOnScreenClicked, MapsOnScreenDismissed
  - LayerActionsClicked(layerId), LayerActionsDismissed
  - MoveLayerUpClicked, MoveLayerDownClicked
  - RemoveLayerClicked
  - LayerOpacityClicked, LayerOpacityChanged(value), LayerOpacityDismissed
- State:
  - `isMenuVisible: Boolean`
  - `isAvailableMapsSheetVisible: Boolean`
  - `availableMapCatalog: List<MapCatalogItem>`
  - `selectedAvailableMap: MapCatalogItem?`
  - `isMapsOnScreenSheetVisible: Boolean`
  - `selectedOverlayLayer: MapLayerEntry?`
  - `editingOverlayOpacityLayer: MapLayerEntry?`
  - `layers: List<MapLayerEntry>`
  - `selectedStyle: MapStyle`
- Reducer:
  - Menu open/close
  - Sheet visibility
  - Layer operations (move, remove, opacity)
  - Catalog selection
- Executor:
  - Minimal (operations are pure)
- Label:
  - LayersChanged(layers)

## Migration Strategy
1. Create tools package
2. Define ToolsComponent API
3. Implement ToolsStore
4. Migrate MapLayerManagementReducer logic
5. Create Factory and DefaultToolsComponent
6. Write tests

## Risks and Mitigations
- Risk: Layer index management errors
  - Mitigation: Thorough unit tests for move operations

## Verification
- `./gradlew :composeApp:compileDebugKotlinAndroid`
- `./gradlew :composeApp:compileKotlinJvm`
- `./gradlew :composeApp:test`
- Unit test: Layer move up/down
- Unit test: Layer remove
- Unit test: Opacity editing

## Task Breakdown
1. Create tools package
2. Define ToolsComponent interface
3. Create ToolsStore structure
4. Migrate layer management logic
5. Implement Reducer
6. Create Factory and DefaultToolsComponent
7. Write unit tests
8. Verify build and tests
