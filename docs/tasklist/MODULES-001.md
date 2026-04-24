# Tasklist — MODULES-001

- [x] Read PRD
- [x] Read plan
- [x] Create module directory structure
- [x] Create api module build script
- [x] Create impl module build script
- [x] Move api code to api module
- [x] Move impl code to impl module
- [x] Update composeApp dependencies
- [x] Remove direct composeApp -> api dependency
- [x] Remove duplicated drawing models in map api
- [x] Verify api module builds
- [x] Verify impl module builds
- [x] Verify composeApp builds
- [x] Run tests
- [x] Update this tasklist
- [x] Prepare commit message

## Completion

- Status: DONE
- Verification command: `./gradlew :feature:map:api:build` `./gradlew :feature:map:impl:build --rerun-tasks` `./gradlew :composeApp:build --rerun-tasks`
- Verification result: SUCCESS
- Suggested commit message: `Fix map module dependency direction and remove duplicated api models`

## Expected Results
| Check | Status |
|-------|--------|
| api module created | DONE |
| impl module created | DONE |
| composeApp depends on impl | DONE |
| no code duplication | DONE |
| Build successful | DONE |
| Tests pass | DONE |
