# Plan — MAP-API-001

## Summary
Create narrow interfaces for child components and hide concrete types from public API.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/` (add interfaces)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/MapScreenUiContract.kt` (modify)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/MapScreenChildComponents.kt` (modify or delete)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/location/LocationComponent.kt` (implement interface)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/drawing/DrawingComponent.kt` (implement interface)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/ruler/RulerComponent.kt` (implement interface)
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/tools/ToolsComponent.kt` (implement interface)

## Implementation Notes

### Step 1: Create Narrow Interfaces

Create in `feature.map.api`:

```kotlin
interface LocationUiContract {
    val model: Value<LocationModel>
    fun onGpsToggle()
    fun onMyLocationClick()
    fun onCurrentLocationFocusClick()
    fun onLocationResult(result: LocationRequestResult)
    fun onLocationRequestConsumed()
}

interface DrawingUiContract {
    val model: Value<DrawingModel>
    val pointSheetSlot: Value<ChildSlot<*, DrawingComponent.PointSheetChild>>
    val shapeSheetSlot: Value<ChildSlot<*, DrawingComponent.ShapeSheetChild>>
    fun onCreatePointClick()
    fun onCreateLineClick()
    // ... other UI methods
}

// Similar for RulerUiContract, ToolsUiContract
```

### Step 2: Update Child Components

Make existing components implement interfaces:

```kotlin
internal class DefaultLocationComponent(...) : LocationComponent, LocationUiContract
```

### Step 3: Update MapScreenChildComponents

```kotlin
interface MapScreenChildComponents {
    val locationUi: LocationUiContract
    val drawingUi: DrawingUiContract
    val rulerUi: RulerUiContract
    val toolsUi: ToolsUiContract
}
```

### Step 4: Update MapScreenUiContract

```kotlin
interface MapScreenUiContract : 
    MapScreenComponent,
    MapScreenToolsHost,
    MapScreenLocationHost,
    MapScreenViewportHost,
    MapScreenDrawingHost,
    MapScreenFeatureHost {
    // Remove MapScreenChildComponents from here
    // Or keep only narrow interfaces
}
```

### Step 5: Update Host Implementation

```kotlin
internal class DefaultMapHostComponent(...) : MapScreenUiContract {
    // Internal: create concrete components
    private val locationComponent: DefaultLocationComponent = ...
    
    // Public: expose via interface
    override val locationUi: LocationUiContract = locationComponent
    override val drawingUi: DrawingUiContract = drawingComponent
    // ...
}
```

### Step 6: Update UI (if needed)

UI should work with minimal or no changes since interfaces expose same methods.

## Risks
- Risk 1: UI may need updates if methods missing from interface
- Risk 2: Slot navigation types may expose internal details
- Risk 3: Tests may reference concrete types

## Verification
- `./gradlew :composeApp:compileDebugKotlin`
- `./gradlew :composeApp:test`
- Check: no `import feature.map.drawing.DrawingComponent` in UI or API

## Task Breakdown
1. [ ] Analyze UI usage of child components (what methods are used)
2. [ ] Design narrow interfaces based on UI needs
3. [ ] Create LocationUiContract
4. [ ] Create DrawingUiContract
5. [ ] Create RulerUiContract
6. [ ] Create ToolsUiContract
7. [ ] Update child components to implement interfaces
8. [ ] Update MapScreenChildComponents
9. [ ] Update DefaultMapHostComponent
10. [ ] Verify UI compiles
11. [ ] Run tests
