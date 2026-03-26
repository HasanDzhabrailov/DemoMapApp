# Tasklist — ROOT-001

- [x] Read PRD
- [x] Read plan
- [x] Confirm affected modules only
- [x] Identify current map component creation path
- [x] Identify all places where the map component is created outside root
- [x] Move map component creation to `DefaultRootComponent`
- [x] Ensure `RootComponent` owns the component lifecycle
- [x] Replace local UI or feature-level creation with root-owned instance usage
- [x] Ensure map rendering behavior remains unchanged
- [x] Ensure no business logic is added to map rendering code
- [x] Ensure Compose remains UI-only
- [x] Add or update tests if needed
- [ ] Run `./gradlew ktlintCheck` (task not configured; verified available Gradle tasks instead)
- [ ] Run `./gradlew detekt` (task not configured)
- [x] Run `./gradlew test`
- [x] Update this tasklist
- [x] Prepare concise diff summary
- [x] Prepare commit message
