# Plan — MAP-ARCH-001

## Summary
Update architecture guidance so future map refactor tickets follow explicit Decompose and modularization rules.

## Affected Modules
- `AGENTS.md`

## Implementation Notes
- Add rules for per-feature component ownership.
- Add rules for per-feature store ownership.
- Add rules for `childContext(key)` and official Decompose navigation models.
- Add package-boundary rules for future modularization.
- Add guardrails against central orchestration growth.
- Add ticket decomposition rules.

## Risks
- Risk 1: Rules become too abstract.
- Risk 2: Rules conflict with existing tasks if wording is too broad.

## Verification
- Read updated `AGENTS.md`.
- Confirm required rules are present and unambiguous.

## Task Breakdown
1. Identify missing architecture rules in current `AGENTS.md`.
2. Draft new rules for Decompose child ownership.
3. Draft new rules for feature and package ownership.
4. Draft ticket sizing and decomposition rules.
5. Review wording for minimal ambiguity.
6. Verify final rules cover current map refactor direction.
