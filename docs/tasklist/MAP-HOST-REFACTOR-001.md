# Tasklist — MAP-HOST-REFACTOR-001

## Phase 0: Analysis
- [x] Read PRD
- [x] Read plan
- [ ] Document all `sync*State()` methods and their purpose
- [ ] Document overlay dismissal triggers and rules
- [ ] Document viewport command flow and sources
- [ ] Document business rules in component methods
- [ ] Analyze MapHostRouterStateMappers usage

## Phase 1: Design
- [ ] Design store state for overlay management
- [ ] Design store state for command routing
- [ ] Design business rule representation
- [ ] Choose state sync elimination approach (A/B/C)
- [ ] Design new intents and messages
- [ ] Review design against AGENTS.md

## Phase 2: Store Changes
- [ ] Add overlay state to MapRouterStore.State
- [ ] Add intents: OverlayOpened, OverlayDismissed, DismissConflictingOverlays
- [ ] Add intents: ViewportCommandIssued, ViewportCommandConsumed
- [ ] Add state: isCenterMarkerEnabled (or similar)
- [ ] Add messages for state updates
- [ ] Update reducer to handle new messages

## Phase 3: Executor Logic
- [ ] Implement overlay coordination logic
- [ ] Implement command routing logic
- [ ] Implement business rule checks
- [ ] Add labels for UI actions

## Phase 4: Host Refactor
- [ ] Remove syncToolsState()
- [ ] Remove syncDrawingState()
- [ ] Remove syncLocationState()
- [ ] Remove syncRulerState()
- [ ] Remove syncViewportState()
- [ ] Refactor onViewportCommandRequested to delegate
- [ ] Remove business rules from onCenterMarkerClick()
- [ ] Update label handling

## Phase 5: Verification
- [ ] Compile check
- [ ] Run unit tests
- [ ] Run JVM tests
- [ ] Manual test: overlay interactions
- [ ] Manual test: viewport commands
- [ ] Manual test: drawing mode blocking
- [ ] Line count verification (< 150)
- [ ] Update this tasklist
- [ ] Prepare commit message

## Expected Results
| Check | Status |
|-------|--------|
| All sync*State() removed | PENDING |
| Host < 150 lines | PENDING |
| Overlay logic in executor | PENDING |
| Command routing in executor | PENDING |
| Business rules in store | PENDING |
| No behavioral regression | PENDING |
| Tests pass | PENDING |
