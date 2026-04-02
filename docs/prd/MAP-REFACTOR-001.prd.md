# MAP-REFACTOR-001 — Router Store Architecture Foundation

## Context
Текущая архитектура имеет монолитный MapStore со 130+ строками State и 50+ callback в UI. Требуется разделение на изолированные компоненты с explicit коммуникацией (не EventBus).

## Goal
Создать Router Store для агрегации дочерних состояний без глобальных flows. Router Store (а не Component) агрегирует MapState для renderer.

## Non-Goals
- Не создавать EventBus (нарушает AGENTS.md line 125-128)
- Не делать business logic в Component (нарушает AGENTS.md line 76)
- Не менять существующие feature работающие вне map

## Functional Requirements
- Router Store объединяет State из дочерних компонентов
- Агрегация MapState происходит в Store (pure function), не в Component
- Коммуникация детей с родителем через Output callback interface
- Component только держит детей и перенаправляет их Output в Router Store

## Acceptance Criteria
- [ ] Router Store создан с State содержащим поля для всех дочерних состояний
- [ ] MapState агрегируется через computed property в State
- [ ] Нет использования SharedFlow/EventBus для коммуникации
- [ ] Component не содержит business logic агрегации

## Constraints
- Follow `AGENTS.md` strictly
- No global mutable state
- Component = lifecycle holder + navigation bridge only
- Reducer must be pure

## Success Metrics
- MapState агрегируется в одном месте (Router Store)
- Явные зависимости между компонентами (через constructor/Output)
- Легко тестируется (mock callbacks вместо global state)
