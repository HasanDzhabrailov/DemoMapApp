# MAP-REFACTOR-002 — Drawing Component and Store

## Context
Создание точек, линий, полигонов сейчас размазано по `DrawingHandler`, `CreatePointHandler` и монолитному `MapStore`. Требуется полная изоляция Drawing feature.

## Goal
Создать полностью изолированный Drawing Component со своим Store. Коммуникация через Output callback (explicit), не EventBus.

## Non-Goals
- Не менять логику создания фич (use cases остаются)
- Не добавлять новые типы фич
- Не трогать другие компоненты

## Functional Requirements
- Drawing Component управляет points, lines, polygons
- Собственный Store со State: lists + draft states
- Output callback для публикации FeatureCreated
- Use cases вызываются в Executor (side effects)

## Acceptance Criteria
- [ ] DrawingComponent interface создан с Output callback
- [ ] DrawingModel содержит только drawing-related поля (~10 полей)
- [ ] DrawingStore имеет State/Intent/Label
- [ ] FeatureCreated публикуется через Output (не EventBus)
- [ ] Use cases вызываются в Executor (CreatePoint, CreateLine, CreatePolygon)

## Constraints
- Follow `AGENTS.md`
- Reducer pure (no time, no IO)
- Executor handles all side effects
- No business logic in Component

## Success Metrics
- Drawing полностью изолирован
- Можно тестировать независимо
- Output callback явный и type-safe
