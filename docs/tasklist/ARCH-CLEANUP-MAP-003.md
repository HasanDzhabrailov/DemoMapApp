# Tasklist - ARCH-CLEANUP-MAP-003

- [x] Read PRD
- [x] Read plan
- [x] Confirm scope: viewport-command cleanup only

- [x] Reduce viewport command routing glue in map host

- [x] Run `./gradlew :composeApp:test`

- [x] Update this tasklist
- [x] Fix viewport component startup crash caused by child slot initialization order
- [x] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| Viewport command routing no longer depends on host orchestration logic beyond minimal delegation | DONE |
