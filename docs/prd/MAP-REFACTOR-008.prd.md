# MAP-REFACTOR-008 — UI Refactoring and Tests

## Context
UI сейчас монолитный (MapScreenOverlays с 50+ callback параметрами). Нужно разделение по компонентам.

## Goal
UI разделение. Удаление монолитных composable с 50+ callback'ами. Каждый UI знает только свой компонент.

## Non-Goals
- Не менять дизайн UI
- Не добавлять новые анимации
- Не менять MapLibre интеграцию

## Functional Requirements
- MapScreenContent как корневой UI
- Каждый overlay получает только свой компонент (~5 intents)
- Удаление MapScreenOverlays (50+ параметров)
- Новые тесты для всех компонентов

## Acceptance Criteria
- [ ] MapScreenOverlays.kt удалён
- [ ] MapScreenContent.kt обновлён (агрегирует детей)
- [ ] DrawingContent.kt создан (только DrawingComponent)
- [ ] RulerOverlay.kt создан (только RulerComponent)
- [ ] LocationControls.kt создан (только LocationComponent)
- [ ] ViewportControls.kt создан (только ViewportComponent)
- [ ] ToolsOverlay.kt создан (только ToolsComponent)
- [ ] Все unit тесты проходят
- [ ] Все integration тесты проходят

## Constraints
- Follow `AGENTS.md`
- Composables only render state and emit intents
- No business logic in UI
- No state ownership in UI

## Success Metrics
- 50+ callback parameters → ~5 per component
- Изолированные UI модули
- Высокое покрытие тестами
