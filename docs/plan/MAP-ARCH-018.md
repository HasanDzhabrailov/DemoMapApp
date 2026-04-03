# Plan — MAP-ARCH-018

## Summary
Migrate tools-related ephemeral UI flows to official Decompose navigation models.

## Affected Modules
- `feature/map/tools`
- `feature/map/ui`

## Implementation Notes
- Use `ChildSlot` where a temporary child component is appropriate.
- Keep scope limited to tools-related flows.

## Risks
- Risk 1: Over-migrating simple UI visibility into unnecessary navigation.

## Verification
- `./gradlew :composeApp:test`
- `./gradlew :composeApp:compileDebugKotlinAndroid`

## Task Breakdown
1. Identify tools flows suitable for navigation.
2. Introduce a navigation model.
3. Wire UI.
4. Run tests and compile.
