# MAP-REFACTOR-006 — Tools Component and Store

## Context
Tools management (меню инструментов, доступные карты, управление слоями) сейчас в `MapLayerManagementReducer`.

## Goal
Создать изолированный Tools Component. Выносим `MapLayerManagementReducer`.

## Non-Goals
- Не менять layer catalog
- Не добавлять новые типы слоёв
- Не менять opacity logic

## Functional Requirements
- Tools Component управляет меню, каталогом карт, слоями
- Output callback для LayersChanged
- Поддержка всех layer операций (add, move, remove, opacity)

## Acceptance Criteria
- [ ] ToolsComponent interface создан
- [ ] ToolsModel содержит menu state, catalog, layers
- [ ] All layer operations work (move up/down, remove, opacity)
- [ ] Output callback отправляет LayersChanged

## Constraints
- Follow `AGENTS.md`
- Reducer pure
- Executor minimal (layer operations are pure)
- No business logic in Component

## Success Metrics
- Tools изолирован
- Layer management работает независимо
- Output callback явный
