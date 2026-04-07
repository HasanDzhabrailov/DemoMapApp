# Ревью MAP-API-001 — Narrow map API to hide internal child components

**Дата ревью:** 2026-04-07  
**Статус:** ✅ APPROVED

---

## Сводка изменений

Реализован тикет MAP-API-001 по сужению публичного API карты через введение узких UI-контрактов.

---

## Проверки

### ✅ 1. API Package — отсутствие internal imports

**Результат:** ПРОЙДЕНО

```bash
$ grep -r "^import ru\.tech\.demomapapp\.feature\.map\.(location|drawing|ruler|tools|viewport)" composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map/api/
No internal imports found - GOOD!
```

Все модели (`LocationModel`, `DrawingModel`, `RulerModel`, `ToolsModel`, `ViewportModel`) теперь определены в API пакете. Внутренние пакеты используют `typealias` для обратной совместимости.

---

### ✅ 2. Соответствие PRD требованиям

**Результат:** ПРОЙДЕНО

| Критерий PRD | Статус |
|--------------|--------|
| Созданы LocationUiContract, DrawingUiContract, RulerUiContract, ToolsUiContract | ✅ |
| Интерфейсы содержат только UI-релевантные методы | ✅ |
| Child компоненты реализуют интерфейсы | ✅ |
| MapScreenChildComponents экспортирует интерфейсы вместо concrete типов | ✅ |
| API package не импортирует из internal child areas | ✅ |
| MapScreenContent компилируется | ✅ |
| Проект компилируется и тесты проходят | ✅ |

---

### ✅ 3. Component → Store → UI архитектура

**Результат:** ПРОЙДЕНО

- **Component:** `Default*Component` реализуют как UI contract (`*UiContract`), так и internal interface (`*Component`)
- **Store:** Не изменены — Store остаются в internal пакетах
- **UI:** UI компоненты теперь зависят только от `*UiContract` интерфейсов

```
┌─────────────────┐
│  MapScreenContent│
│  (uses *UiContract)│
└────────┬────────┘
         │
┌────────▼────────┐
│*UiContract      │  ← API package
│(narrow interface)│
└────────┬────────┘
         │
┌────────▼────────┐
│*Component       │  ← internal package
│(extends *UiContract)│
└────────┬────────┘
         │
┌────────▼────────┐
│Default*Component│
│(implementation) │
└─────────────────┘
```

---

### ✅ 4. Reducer чистота (pure functions)

**Результат:** ПРОЙДЕНО

Reducers не изменены. Все по-прежнему:
- Чистые функции
- Нет side effects
- Нет IO операций
- Нет доступа к platform API

---

### ✅ 5. Error Handling

**Результат:** ПРОЙДЕНО

Error handling не изменен — следует существующим паттернам проекта:
- Errors нормализуются на executor side
- Persistent errors → State
- One-time errors → Labels

---

### ✅ 6. Тесты

**Результат:** ПРОЙДЕНО

```bash
$ ./gradlew :composeApp:test
BUILD SUCCESSFUL in 47s
54 actionable tasks: 12 executed, 42 up-to-date
```

Все существующие тесты проходят без изменений.

---

## Файлы изменены

### Новые файлы API:
- `LocationUiContract.kt` — UI контракт + LocationModel
- `DrawingUiContract.kt` — UI контракт + DrawingModel, CreatePointDraft, ShapeDrawingDraft, DrawingMode
- `RulerUiContract.kt` — UI контракт + RulerModel
- `ToolsUiContract.kt` — UI контракт + ToolsModel
- `ViewportUiContract.kt` — UI контракт + ViewportModel

### Измененные файлы:
- `MapScreenUiContract.kt` — использует UiContract интерфейсы
- `LocationComponent.kt`, `DrawingComponent.kt`, `RulerComponent.kt`, `ToolsComponent.kt`, `ViewportComponent.kt` — extend UiContract
- `DefaultMapHostComponent.kt` — private компоненты, public UiContract
- `MapScreenContent.kt`, `MapScreenPreview.kt` — используют UiContract
- UI файлы — обновлены для использования UiContract
- Модели location, drawing, ruler, tools, viewport — заменены на typealias

---

## Найденные и исправленные проблемы

### Проблема 1: API package импортировал модели из internal пакетов
**Статус:** ИСПРАВЛЕНО

**Было:**
```kotlin
// LocationUiContract.kt
import ru.tech.demomapapp.feature.map.location.LocationModel
```

**Стало:**
```kotlin
// LocationUiContract.kt — LocationModel определен здесь
// LocationModel.kt (internal) — typealias
```

---

## Риски

| Риск | Уровень | Митигация |
|------|---------|-----------|
| Typealias для моделей может сбить с толку | Низкий | Документация в файлах |
| fromModel() function moved to top-level | Низкий | Import добавлен в DefaultToolsComponent |

---

## Рекомендации

1. **Для MAP-API-002:** Рассмотреть State-Driven подход (Option B из PRD) для полного удаления child component exposure
2. **Тестирование:** Добавить интеграционные тесты на проверку API boundaries
3. **Документация:** Обновить ARCHITECTURE.md с описанием UiContract паттерна

---

## Итог

**Решение:** ✅ APPROVE

Все требования PRD выполнены. API package теперь не зависит от internal child packages. Архитектура Component → Store → UI сохранена. Все тесты проходят.
