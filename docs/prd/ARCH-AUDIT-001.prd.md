# ARCH-AUDIT-001 — Audit helper, glue, and package violations across repository

## Context
- The map refactor should become the reference pattern before broader repo cleanup.
- Repository-wide helper, glue, and package issues should be inventoried before cleanup tickets are created.

## Goal
Audit the repository and produce a categorized inventory of helper, glue, and package-boundary violations.

## Non-Goals
- No broad cleanup in this ticket.
- No production behavior changes.

## User Scenarios
- Architect can create small cleanup tickets from a reviewed inventory.

## Acceptance Criteria
- [ ] Repository areas with helper, glue, or package violations are inventoried.
- [ ] Findings are grouped by business area.
- [ ] Follow-up cleanup tickets can be generated from the audit.

## Constraints
- Inventory only.
- No broad refactor.

## Success Metrics
- Cleanup work is driven by evidence rather than assumptions.

## Open Questions
- Should audit severity be ranked by architectural risk or by cleanup effort?
