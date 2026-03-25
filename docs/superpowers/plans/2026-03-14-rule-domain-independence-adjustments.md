# Rule Domain Independence Adjustments Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make rule domains independent and align rule ownership so package-placement and naming rules live in their intended domain modules.

**Architecture:** Keep each `rules/<domain>` package self-contained with no imports from other rule-domain packages. Move package-placement rules to `rules/packages` and move all naming rules into `rules/naming` with direct implementations (no cross-domain delegation). Keep behavior and assertion messages stable while updating contexts/presets/tests to the new ownership model.

**Tech Stack:** Kotlin, Gradle, Konsist, JUnit 5.

---

## Chunk 1: Contract And Failing Tests First

### Task 1: Add failing tests for new ownership and package-name acceptance

**Files:**
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/ContextBoundaryContractTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/PackageRulesViolationTest.kt`
- Create: `src/test/kotlin/fixtures/violations/core/configuration/ConfigurationPackageConfigurationPositive.kt`

- [ ] **Step 1: Add failing test for `configurationPackageRule` accepting `.configuration` package**
Add a positive test in `DependencyInjectionAndCoreRulesViolationTest` that verifies `ConfigurationRules.configurationPackageRule` passes for a fixture under a `.configuration` package.

- [ ] **Step 2: Add failing tests that package-placement rules are exposed by `PackageRules`**
Add tests in `PackageRulesViolationTest` for:
- `servicePackageRule` negative and positive cases using existing service fixtures.
- `controllerPackageRule` negative and positive cases using existing controller package fixtures.

- [ ] **Step 3: Add failing contract test for rules package independence**
In `ContextBoundaryContractTest`, add test to scan all files under `src/main/kotlin/dev/protsenko/codeguard/rules/**` and fail if a file imports `dev.protsenko.codeguard.rules.<other-domain>`.

- [ ] **Step 4: Run targeted tests to verify RED**
Run:
`./gradlew test --tests "*DependencyInjectionAndCoreRulesViolationTest" --tests "*PackageRulesViolationTest" --tests "*ContextBoundaryContractTest"`
Expected: FAIL because code has not yet implemented new ownership/acceptance rules.

- [ ] **Step 5: Commit**
```bash
git add src/test/kotlin/dev/protsenko/codeguard/coverage/ContextBoundaryContractTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/PackageRulesViolationTest.kt src/test/kotlin/fixtures/violations/core/configuration/ConfigurationPackageConfigurationPositive.kt
git commit -m "test: add ownership and independence guardrails for rule domains"
```

---

## Chunk 2: Package Rule Ownership And Wiring

### Task 2: Move package-placement rules into `PackageRules`

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/SpringCoreRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/web/ControllerRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/context/CoreRuleContext.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/context/WebRuleContext.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/context/PackageRuleContext.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/presets/SpringBootPresets.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DslAndPresetsTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/WebRulesViolationTest.kt`

- [ ] **Step 1: Write failing tests for updated context/preset wiring**
Update `DslAndPresetsTest` expectations to match package ownership:
- `PackageRuleContext` should include package naming + service package + controller package rules.
- Core/Web context coverage should no longer count package-placement rules.

- [ ] **Step 2: Run targeted tests to verify RED**
Run:
`./gradlew test --tests "*DslAndPresetsTest" --tests "*PackageRulesViolationTest" --tests "*WebRulesViolationTest"`
Expected: FAIL due missing/miswired package-placement ownership.

- [ ] **Step 3: Implement minimal production changes**
Implement `PackageRules.servicePackageRule` and `PackageRules.controllerPackageRule` using existing behavior/messages.
Remove `servicePackageRule` from `SpringCoreRules` and `controllerPackageRule` from `ControllerRules`.
Rewire contexts and presets to reference `PackageRules` as the source of package-placement rules.

- [ ] **Step 4: Run targeted tests to verify GREEN**
Run:
`./gradlew test --tests "*PackageRulesViolationTest" --tests "*DependencyInjectionAndCoreRulesViolationTest" --tests "*WebRulesViolationTest" --tests "*DslAndPresetsTest"`
Expected: PASS.

- [ ] **Step 5: Commit**
```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt src/main/kotlin/dev/protsenko/codeguard/rules/core/SpringCoreRules.kt src/main/kotlin/dev/protsenko/codeguard/rules/web/ControllerRules.kt src/main/kotlin/dev/protsenko/codeguard/context/CoreRuleContext.kt src/main/kotlin/dev/protsenko/codeguard/context/WebRuleContext.kt src/main/kotlin/dev/protsenko/codeguard/context/PackageRuleContext.kt src/main/kotlin/dev/protsenko/codeguard/presets/SpringBootPresets.kt src/test/kotlin/dev/protsenko/codeguard/coverage/DslAndPresetsTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/PackageRulesViolationTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/WebRulesViolationTest.kt
git commit -m "refactor: move package placement rules into package domain"
```

---

## Chunk 3: Naming Rule Ownership Consolidation

### Task 3: Move all naming rules into `NamingRules` with local implementations

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/naming/NamingRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/SpringCoreRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/web/ControllerRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/jpa/RepositoryRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/CoreExceptionRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/presets/SpringBootPresets.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/WebRulesViolationTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/JpaRulesViolationTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/ExceptionHandlingRulesViolationTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/NamingRulesViolationTest.kt` (create if missing)

- [ ] **Step 1: Write failing tests by switching naming assertions to `NamingRules`**
Update naming-related tests so they call:
- `NamingRules.componentNamingRule`
- `NamingRules.controllerNamingRule`
- `NamingRules.repositoryNamingRule`
- `NamingRules.exceptionHandlerNamingRule`

- [ ] **Step 2: Run targeted tests to verify RED**
Run:
`./gradlew test --tests "*DependencyInjectionAndCoreRulesViolationTest" --tests "*WebRulesViolationTest" --tests "*JpaRulesViolationTest" --tests "*ExceptionHandlingRulesViolationTest"`
Expected: FAIL before rules are moved.

- [ ] **Step 3: Implement naming rules directly inside `NamingRules`**
Copy logic/messages into `NamingRules` so it depends only on Konsist + DSL contracts, not other rule packages.
Remove naming rule declarations from `SpringCoreRules`, `ControllerRules`, `RepositoryRules`, and `CoreExceptionRules`.
Rewire presets and any remaining usages to `NamingRules`.

- [ ] **Step 4: Run targeted tests to verify GREEN**
Run:
`./gradlew test --tests "*DependencyInjectionAndCoreRulesViolationTest" --tests "*WebRulesViolationTest" --tests "*JpaRulesViolationTest" --tests "*ExceptionHandlingRulesViolationTest" --tests "*DslAndPresetsTest"`
Expected: PASS.

- [ ] **Step 5: Commit**
```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/naming/NamingRules.kt src/main/kotlin/dev/protsenko/codeguard/rules/core/SpringCoreRules.kt src/main/kotlin/dev/protsenko/codeguard/rules/web/ControllerRules.kt src/main/kotlin/dev/protsenko/codeguard/rules/jpa/RepositoryRules.kt src/main/kotlin/dev/protsenko/codeguard/rules/core/CoreExceptionRules.kt src/main/kotlin/dev/protsenko/codeguard/presets/SpringBootPresets.kt src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/WebRulesViolationTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/JpaRulesViolationTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/ExceptionHandlingRulesViolationTest.kt src/test/kotlin/dev/protsenko/codeguard/coverage/DslAndPresetsTest.kt
git commit -m "refactor: consolidate naming rules into naming domain"
```

---

## Chunk 4: Configuration Package Rule Expansion

### Task 4: Expand `configurationPackageRule` to accept `.config` and `.configuration`

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/ConfigurationRules.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt`

- [ ] **Step 1: Keep RED for new acceptance case**
Confirm the `.configuration` positive test added in Task 1 is still failing against current implementation.

- [ ] **Step 2: Implement minimal fix**
Update `configurationPackageRule` filter to accept both:
- `..config..`
- `..configuration..`
Update description and assertion message to match accepted package set.

- [ ] **Step 3: Run targeted tests to verify GREEN**
Run:
`./gradlew test --tests "*DependencyInjectionAndCoreRulesViolationTest"`
Expected: PASS.

- [ ] **Step 4: Commit**
```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/core/ConfigurationRules.kt src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt
git commit -m "feat: allow configuration classes in config or configuration packages"
```

---

## Chunk 5: Final Independence Verification And Full Checks

### Task 5: Validate independence and run full quality gates

**Files:**
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/ContextBoundaryContractTest.kt` (if needed for final contract adjustments)
- Modify: `RULES.md` (if ownership descriptions changed)

- [ ] **Step 1: Verify no cross-domain imports inside rules packages**
Run:
`./gradlew test --tests "*ContextBoundaryContractTest"`
Expected: PASS.

- [ ] **Step 2: Run full test suite**
Run:
`./gradlew test`
Expected: PASS.

- [ ] **Step 3: Run static checks**
Run:
`./gradlew codeBaseline`
Expected: PASS with no detekt smells.

- [ ] **Step 4: Commit**
```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules src/main/kotlin/dev/protsenko/codeguard/context src/main/kotlin/dev/protsenko/codeguard/presets src/test/kotlin/dev/protsenko/codeguard/coverage src/test/kotlin/fixtures RULES.md
git commit -m "refactor: enforce independent rule domains and finalized rule ownership"
```

---

## Risks
- API surface changes in contexts can break downstream DSL users if package-placement methods move between contexts.
- Assertion message drift during rule relocation may break existing tests.
- Hidden transitive imports between rule domains can reappear during refactor.

## Mitigations
- Keep tests red/green at each move.
- Preserve error message text unless intentionally updated with corresponding tests.
- Keep `ContextBoundaryContractTest` as a permanent independence guardrail.

## Definition Of Done
- `configurationPackageRule` accepts both `.config` and `.configuration`.
- `servicePackageRule` and `controllerPackageRule` are defined in `rules/packages/PackageRules.kt`.
- All naming rules are defined directly in `rules/naming/NamingRules.kt`.
- No file in `src/main/kotlin/dev/protsenko/codeguard/rules/**` imports rule classes from another rule domain package.
- `./gradlew test` and `./gradlew codeBaseline` pass.
