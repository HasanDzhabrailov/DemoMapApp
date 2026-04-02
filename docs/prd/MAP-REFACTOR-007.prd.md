# MAP-REFACTOR-007 — MapScreenComponent Router (FINAL)

## Context
Финальный этап — замена монолитного MapStore на Router Component с child components.

## Goal
Router Component. Удаление монолитного MapStore. Component = только lifecycle holder + navigation bridge (no business logic).

## Non-Goals
- Не менять public API MapScreenComponent (фасад)
- Не добавлять новые фичи
- Не менять UI контракт (только реализацию)

## Functional Requirements
- MapScreenComponent держит дочерние компоненты
- Агрегация Model через Router Store (не Component)
- Все дети коммуницируют через Output callbacks
- Удаление всех старых файлов MapStore

## Acceptance Criteria
- [ ] Все старые MapStore файлы удалены
- [ ] DefaultMapScreenComponent создан как Router
- [ ] Все child components созданы и подключены
- [ ] Model агрегируется в Router Store (не Component)
- [ ] Output callbacks перенаправляются в Router Store
- [ ] Build passes, все тесты работают

## Constraints
- Follow `AGENTS.md` strictly
- Component = lifecycle holder + Store holder + navigation bridge
- Component contains NO business logic
- Reducer pure, Executor handles side effects
- No global flows

## Success Metrics
- Монолитный MapStore удалён
- 5 изолированных компонентов работают
- Code coverage сохранена или улучшена
