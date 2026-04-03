# Plan — MAP-ARCH-002

## Summary
Produce a target package map and dependency policy for `feature/map` before any physical code moves.

## Affected Modules
- `composeApp/src/commonMain/kotlin/ru/tech/demomapapp/feature/map`

## Implementation Notes
- Define top-level areas: `api`, `host`, `mapscreen`, `tools`, `drawing`, `location`, `ruler`, `viewport`.
- Document ownership of each area.
- Document import and dependency direction.
- Document rules for shared code placement.
- Keep structure shallow unless a package already has divergent responsibilities.

## Risks
- Risk 1: Overdesign before actual migration.
- Risk 2: Too much nesting reduces clarity instead of improving it.

## Verification
- Review target package map.
- Confirm each current responsibility has one target destination.
- Confirm package dependency directions are acyclic by design.

## Task Breakdown
1. Inventory current map responsibilities.
2. Assign each responsibility to a target package area.
3. Define allowed package dependencies.
4. Define rules for shared map code.
5. Review target structure for modularization readiness.
6. Finalize package map for follow-up tickets.
