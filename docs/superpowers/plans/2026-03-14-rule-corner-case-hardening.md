# Rule Corner Case Hardening Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move package-placement rules to the packages domain and harden core/web/jpa rules against known false positives/false negatives with full regression coverage.

**Architecture:** Keep rule ownership aligned with DSL domains: package placement rules belong to `rules.packages` and `packages {}` context. Strengthen rule logic by replacing brittle string checks with explicit declaration/type parsing where possible. Add focused fixtures and failing tests first, then implement minimal production changes and re-verify full suite.

**Tech Stack:** Kotlin, Konsist, JUnit 5, Gradle (`test`, `codeBaseline`)

---

## Chunk 1: Domain Ownership + Core Rule Hardening

### Task 1: Move Configuration Package Rules To Packages Domain

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/ConfigurationRules.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/context/CoreRuleContext.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/context/PackageRuleContext.kt`
- Modify: `src/main/kotlin/dev/protsenko/codeguard/presets/SpringBootPresets.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DslAndPresetsTest.kt`

- [ ] **Step 1: Write failing tests for domain ownership move**

Add/adjust tests so package-placement checks are asserted through `PackageRules` and `PackageRuleContext` methods:
- `configurationPackageRule` and `propertiesValidationRule` assertions should reference `PackageRules`.
- `CoreRuleContext` count drops (remove config-package/property-package methods).
- `PackageRuleContext` count increases (add these methods there).

- [ ] **Step 2: Run tests to verify RED**

Run:  
`./gradlew test --tests dev.protsenko.codeguard.coverage.DependencyInjectionAndCoreRulesViolationTest --tests dev.protsenko.codeguard.coverage.DslAndPresetsTest`

Expected: FAIL due to moved-method/rule references not yet implemented.

- [ ] **Step 3: Minimal implementation**

Move these rule definitions from `ConfigurationRules` to `PackageRules`:
- `configurationPackageRule`
- `propertiesValidationRule`

Update contexts:
- Remove `configurationInConfigPackage()` and `configurationPropertiesInPropertiesPackage()` from `CoreRuleContext`.
- Add those methods to `PackageRuleContext` and point to `PackageRules`.

Update preset:
- `SpringBootPresets.STYLE_CONVENTIONS` should use package-domain versions.

- [ ] **Step 4: Run tests to verify GREEN**

Run same command as Step 2.  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt \
  src/main/kotlin/dev/protsenko/codeguard/rules/core/ConfigurationRules.kt \
  src/main/kotlin/dev/protsenko/codeguard/context/CoreRuleContext.kt \
  src/main/kotlin/dev/protsenko/codeguard/context/PackageRuleContext.kt \
  src/main/kotlin/dev/protsenko/codeguard/presets/SpringBootPresets.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/DslAndPresetsTest.kt
git commit -m "refactor: move config package rules to packages domain"
```

### Task 2: Allow `.property` Package Alias For Configuration Properties

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt`
- Create: `src/test/kotlin/fixtures/violations/core/config/PropertiesValidationPropertyPositive.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt`

- [ ] **Step 1: Write failing test**

Add positive test:
- `propertiesValidationRule passes for ConfigurationProperties in property package`

- [ ] **Step 2: Run test to verify RED**

Run:  
`./gradlew test --tests dev.protsenko.codeguard.coverage.DependencyInjectionAndCoreRulesViolationTest`

Expected: FAIL because `.property` is not yet accepted.

- [ ] **Step 3: Minimal implementation**

In `propertiesValidationRule`, accept both:
- `..properties..`
- `..property..`

- [ ] **Step 4: Run test to verify GREEN**

Run same command as Step 2.  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt \
  src/test/kotlin/fixtures/violations/core/config/PropertiesValidationPropertyPositive.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt
git commit -m "feat: allow property package for configuration properties rule"
```

### Task 3: Fix Brittle Bean-Method Ownership Matching

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/ConfigurationRules.kt`
- Create: `src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsNameCollisionPositive.kt`
- Create: `src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsNameCollisionNegative.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt`

- [ ] **Step 1: Write failing tests**

Add cases:
- Positive: bean method in `AppConfig` should pass even if `AppConfigHelper` exists.
- Negative: bean method in helper/non-config class should fail.

- [ ] **Step 2: Run tests to verify RED**

Run:  
`./gradlew test --tests dev.protsenko.codeguard.coverage.DependencyInjectionAndCoreRulesViolationTest`

Expected: FAIL due to current substring-based matching.

- [ ] **Step 3: Minimal implementation**

Replace substring check with declaration-aware check:
- Resolve containing declaration and compare against configuration declarations directly (not `contains` string matching).

- [ ] **Step 4: Run tests to verify GREEN**

Run same command as Step 2.  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/core/ConfigurationRules.kt \
  src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsNameCollisionPositive.kt \
  src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsNameCollisionNegative.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/DependencyInjectionAndCoreRulesViolationTest.kt
git commit -m "fix: make bean method configuration check declaration-safe"
```

### Task 4: Fix `loggerInsteadOfPrintRule` False Positives

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/core/CoreObservabilityRules.kt`
- Create: `src/test/kotlin/fixtures/violations/observability/LoggerInsteadOfPrintNameContainsPrintPositive.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/ObservabilityRulesViolationTest.kt`

- [ ] **Step 1: Write failing test**

Add positive test where method calls include names like `blueprint(...)` or `sprint(...)` and should not be treated as console print.

- [ ] **Step 2: Run test to verify RED**

Run:  
`./gradlew test --tests dev.protsenko.codeguard.coverage.ObservabilityRulesViolationTest`

Expected: FAIL with current naive substring search.

- [ ] **Step 3: Minimal implementation**

Use stricter regex/token matching for console output only:
- Match `\bprintln\s*\(` or `\bprint\s*\(`
- Keep existing violation message format stable.

- [ ] **Step 4: Run test to verify GREEN**

Run same command as Step 2.  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/core/CoreObservabilityRules.kt \
  src/test/kotlin/fixtures/violations/observability/LoggerInsteadOfPrintNameContainsPrintPositive.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/ObservabilityRulesViolationTest.kt
git commit -m "fix: avoid false positives in logger instead of print rule"
```

## Chunk 2: Web/JPA Type Handling Hardening

### Task 5: Expand DTO Separation Type Unwrapping

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/web/ResponseHandlingRules.kt`
- Create: `src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationSetNegative.kt`
- Create: `src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationPageNegative.kt`
- Create: `src/test/kotlin/fixtures/violations/web/responsehandling/PageStub.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/WebRulesViolationTest.kt`

- [ ] **Step 1: Write failing tests**

Add negative tests for:
- `Set<UserEntity>` return/parameter
- `Page<UserEntity>` return/parameter
- nested wrappers where practical in fixture scope.

- [ ] **Step 2: Run tests to verify RED**

Run:  
`./gradlew test --tests dev.protsenko.codeguard.coverage.WebRulesViolationTest`

Expected: FAIL because current unwrapping only strips `ResponseEntity<>` and `List<>`.

- [ ] **Step 3: Minimal implementation**

Implement shared type normalization helper in `ResponseHandlingRules`:
- strip nullability (`?`)
- unwrap recursive generic wrappers (`ResponseEntity`, `List`, `Set`, `Collection`, `Page`, etc.)
- inspect terminal type against entity set.

- [ ] **Step 4: Run tests to verify GREEN**

Run same command as Step 2.  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/web/ResponseHandlingRules.kt \
  src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationSetNegative.kt \
  src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationPageNegative.kt \
  src/test/kotlin/fixtures/violations/web/responsehandling/PageStub.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/WebRulesViolationTest.kt
git commit -m "fix: strengthen dto separation wrapper type detection"
```

### Task 6: Support Inherited `@Id` In Entity Rule

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/jpa/EntityRules.kt`
- Create: `src/test/kotlin/fixtures/violations/jpa/entity/EntityIdInheritedPositive.kt`
- Create: `src/test/kotlin/fixtures/violations/jpa/entity/BaseEntityWithId.kt`
- Modify: `src/test/kotlin/dev/protsenko/codeguard/coverage/JpaRulesViolationTest.kt`

- [ ] **Step 1: Write failing test**

Add positive case where entity inherits `@Id` from superclass and must pass.

- [ ] **Step 2: Run test to verify RED**

Run:  
`./gradlew test --tests dev.protsenko.codeguard.coverage.JpaRulesViolationTest`

Expected: FAIL with current direct-property-only check.

- [ ] **Step 3: Minimal implementation**

Update `entityIdRule` to account for inherited ID:
- check direct properties
- if absent, traverse parent class chain and check parent properties for `@Id`
- keep failure message unchanged.

- [ ] **Step 4: Run test to verify GREEN**

Run same command as Step 2.  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/jpa/EntityRules.kt \
  src/test/kotlin/fixtures/violations/jpa/entity/EntityIdInheritedPositive.kt \
  src/test/kotlin/fixtures/violations/jpa/entity/BaseEntityWithId.kt \
  src/test/kotlin/dev/protsenko/codeguard/coverage/JpaRulesViolationTest.kt
git commit -m "fix: accept inherited id in entity id rule"
```

### Task 7: Docs + End-to-End Verification

**Files:**
- Modify: `RULES.md`
- Modify (if needed): `src/test/kotlin/dev/protsenko/codeguard/coverage/ContextBoundaryContractTest.kt`

- [ ] **Step 1: Update docs for moved rule ownership**

Ensure `RULES.md` reflects:
- `configurationPackageRule` and `propertiesValidationRule` in package/style domain.
- any adjusted wording for `.property` alias support.

- [ ] **Step 2: Run full verification**

Run:
- `./gradlew test`
- `./gradlew codeBaseline`

Expected: both PASS.

- [ ] **Step 3: Final commit**

```bash
git add RULES.md src/test/kotlin/dev/protsenko/codeguard/coverage/ContextBoundaryContractTest.kt
git commit -m "docs: align rule ownership and verification coverage"
```

---

## Notes

- Keep all new assertion checks as exact full-message assertions (`assertEquals`) per current test policy.
- Preserve existing message text unless behavior changes require explicit message updates.
- If Konsist model APIs limit robust declaration traversal for a rule, implement the narrowest text fallback and document the limitation in a test name/comment.
