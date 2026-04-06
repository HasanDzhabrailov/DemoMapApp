# Plan — ARCH-CLEANUP-MAP-002

## Summary
Remove manual ruler input synchronization glue from the map host.

## Affected Modules
- `feature/map`

## Implementation Notes
- Resolve only ruler input synchronization in host.
- Keep change minimal and behavior-preserving.

## Risks
- Risk 1: Ruler measurement updates may regress if camera/location propagation changes.

## Verification
- `./gradlew :composeApp:test`

## Task Breakdown
1. Confirm current ruler input synchronization path in `DefaultMapHostComponent`.
2. Move ownership of ruler inputs out of host glue.
3. Run tests.
