# MAP-REFACTOR-004 — Location Component and Store

## Context
Location management (GPS, my location, permissions) сейчас в `LocationHandler`. Нужна изоляция в отдельный компонент.

## Goal
Создать изолированный Location Component. Выносим логику из `LocationHandler`.

## Non-Goals
- Не менять permission handling логику
- Не добавлять новые location режимы
- Не трогать Android-specific location APIs

## Functional Requirements
- Location Component управляет my location mode и current marker
- Output callbacks: LocationUpdated, ViewportCommand, LocationRequestIssued
- Поддержка всех режимов: OFF, MANUAL_PLACEHOLDER, GPS
- Executor запрашивает location (side effect)

## Acceptance Criteria
- [ ] LocationComponent interface создан с Output callback
- [ ] LocationModel содержит mode, currentMarker, pendingRequest
- [ ] Все режимы работают (OFF → MANUAL → GPS)
- [ ] Output callbacks отправляют события

## Constraints
- Follow `AGENTS.md`
- Reducer pure
- Executor handles location requests (side effect)
- No business logic in Component

## Success Metrics
- Location изолирован
- Можно тестировать режимы независимо
- Output callbacks явные
