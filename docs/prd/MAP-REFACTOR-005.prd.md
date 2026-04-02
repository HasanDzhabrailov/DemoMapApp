# MAP-REFACTOR-005 — Viewport Component and Store

## Context
Viewport management (камера, zoom, центральный маркер) — базовая функция карты.

## Goal
Создать изолированный Viewport Component для управления камерой и zoom.

## Non-Goals
- Не менять MapLibre интеграцию
- Не добавлять новые viewport команды
- Не менять zoom levels

## Functional Requirements
- Viewport Component управляет camera snapshot и zoom
- Output callback для ViewportCommand (ZoomIn, ZoomOut)
- Поддержка center marker menu

## Acceptance Criteria
- [ ] ViewportComponent interface создан
- [ ] ViewportModel содержит snapshot, pendingCommand, menu state
- [ ] Zoom in/out генерирует ViewportCommand через Output
- [ ] Camera idle обновляет snapshot

## Constraints
- Follow `AGENTS.md`
- Reducer pure
- Executor minimal (viewport commands go through Label)
- No business logic in Component

## Success Metrics
- Viewport изолирован
- Команды отправляются через Output
- Легко тестировать
