# Archive

This directory contains superseded tickets that are no longer active.

## Superseded Tickets

| Ticket | Status | Superseded By | Reason |
|--------|--------|---------------|--------|
| ARCH-AUDIT-001 | Completed | N/A | Audit completed, findings documented |
| ARCH-CLEANUP-MAP-001 | Superseded | MAP-HOST-REFACTOR-001 | Scope overlap with new host cleanup ticket |
| ARCH-CLEANUP-MAP-002 | Superseded | MAP-HOST-REFACTOR-001 | Specific issue (ruler sync) covered by combined ticket |
| ARCH-CLEANUP-MAP-003 | Superseded | MAP-HOST-REFACTOR-001 | Specific issue (viewport commands) covered by combined ticket |
| ARCH-CLEANUP-MAP-004 | Superseded | MAP-HOST-REFACTOR-001 | Specific issue (overlay exclusivity) covered by combined ticket |
| MAP-HOST-001 | Superseded | MAP-HOST-REFACTOR-001 | Merged into comprehensive host refactor |
| MAP-HOST-002 | Superseded | MAP-HOST-REFACTOR-001 | Merged into comprehensive host refactor |
| MAP-HOST-003 | Superseded | MAP-HOST-REFACTOR-001 | Merged into comprehensive host refactor |
| ARCH-CLEANUP-ROOT-001 | Superseded | ROOT-CLEANUP-001 | Replaced by focused cleanup ticket |
| ARCH-CLEANUP-OTHER-001 | Superseded | OTHER-CLEANUP-001 | Replaced by focused cleanup ticket |

## New Ticket Structure

New cleanup tickets follow refreshed naming convention:
- `ROOT-CLEANUP-*` - Root level cleanup
- `OTHER-CLEANUP-*` - General/utility cleanup
- `MAP-HOST-*` - Map host component decoupling
- `MAP-API-*` - Map API contract simplification
- `MODULES-*` - Gradle module creation
- `MAP-INFRA-*` - Map infrastructure separation
- `MAP-RENDER-*` - Feature-owned map rendering

See `docs/prd/` and `docs/plan/` for active tickets.
