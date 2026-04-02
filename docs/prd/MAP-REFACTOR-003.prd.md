# MAP-REFACTOR-003 — Ruler Component and Store

## Context
Ruler (линейка) зависит от Location для измерений. Нужна изоляция с explicit dependency (не глобальный EventBus).

## Goal
Создать изолированный Ruler Component. Получает Location updates через explicit method от parent.

## Non-Goals
- Не менять алгоритм измерений (RulerMeasurementCalculator)
- Не менять форматирование (RulerInfoWindowStateFormatter)
- Не добавлять новые режимы работы

## Functional Requirements
- Ruler Component управляет enabled state и измерениями
- Подписка на Location через explicit method от parent
- Output callback для ViewportCommand (для обновления измерений)
- Использует существующие calculator/formatter

## Acceptance Criteria
- [ ] RulerComponent interface создан
- [ ] RulerModel содержит isEnabled, measurement, infoWindow
- [ ] `onLocationUpdated(location)` метод для получения обновлений
- [ ] Output callback для ViewportCommand
- [ ] Нет глобальных подписок (EventBus)

## Constraints
- Follow `AGENTS.md`
- Parent явно вызывает `onLocationUpdated` при изменениях Location
- Reducer pure
- Executor может запрашивать вычисления (side effects)

## Success Metrics
- Ruler изолирован и тестируем
- Зависимость от Location явная (через method call)
- Нет скрытых зависимостей
