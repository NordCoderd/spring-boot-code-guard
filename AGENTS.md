# Spring Boot Code Guard

Konsist-based static analysis library for Spring Boot.
Rules run as JUnit 5 tests. Kotlin + Gradle (Kotlin DSL), JVM 17.
Published to Maven Central as `dev.protsenko:spring-boot-code-guard`.

## Commands

- `./gradlew test` — all tests
- `./gradlew test --tests '*PackageRulesViolationTest'` — one test class
- `./gradlew detektMain` — static analysis (config: `detekt.yml`)
- `./gradlew codeBaseline` — full gate: clean, test, detekt, Kover verify (90% floor). Run before finishing any change.

## Layout

```
src/main/kotlin/dev/protsenko/codeguard/
  core/                 # SpringBootRule, DSL entry, suppression, RuleBuilder
  rules/{general,jpa,naming,packages,proxy,web}/   # rule objects + DSL contexts
  rules/SpringAnnotations.kt                       # annotation name constants
src/test/kotlin/
  dev/protsenko/codeguard/coverage/   # violation tests per rule group
  dev/protsenko/codeguard/usage/      # UsageExampleTest (public DSL examples)
  fixtures/violations/                # fixture classes triggered by tests
```

Entry point: `springBootRules { }.verify()` (`core/SpringBootRulesConfiguration.kt`).

## Rule anatomy

Each rule is an `object : SpringBootRule` with `description`, `suppressKey`, `verify(scope)`.
- Per-class silence: `@Suppress("CodeGuard:<key>")`
- DSL-wide opt-out: `exclude("CodeGuard:<key>")`

## Hard rules

- Never suppress detekt/Kover violations, remove or exclude tests, or weaken any quality gate.
- Never lower the Kover `minBound(90)` in `build.gradle.kts`.
- Use `hasAnnotationWithName(SpringAnnotations.*)`, not `hasAnnotationOf`.

## Adding a new rule — mandatory checklist

Follow every step in order. Do not skip.

### 1. TDD — fixtures and tests first

Use the `test-driven-development` skill. Create fixture classes under `src/test/kotlin/fixtures/violations/<category>/<rule>/`:
- `*Negative.kt` — triggers the violation (one per interesting failure case)
- `*Positive.kt` — must pass (one per interesting pass case)

Add test methods to the matching `*ViolationTest.kt` in `src/test/kotlin/dev/protsenko/codeguard/coverage/`.
- Negative test: assert the exact error message.
- Positive test: call `rule.verify(scope)` without `assertFailsWith`.

Cover per rule:
- Wrong stereotype alone in the constrained location → fail
- Correct stereotype alone → pass
- Correct stereotype with file-level helpers in same file → pass
- Unannotated class alone in constrained location → fail

RED phase must fail on the expected behavior, not on a compilation error. Run the `simplify` skill after tests pass.

### 2. Implement the rule

Add the rule `object` to the relevant `*Rules.kt` in `src/main/kotlin/.../rules/<category>/`. Group by `containingFile` when file-level helper exemptions are needed.

### 3. Register in `all*Rules` list

Each `*Rules.kt` exposes `val all*Rules: List<SpringBootRule>` at the bottom. Add the rule there. `AllRulesTest` asserts exact counts — update `allPackageRules contains N rules` (or equivalent).

### 4. Expose in DSL context and `AllRulesTest` individual block

Add a DSL function to the matching `*RuleContext.kt`. Add the call to the `withIndividual` block in `AllRulesTest.kt` next to its peers (e.g. `entitiesInEntityPackage()`).

### 5. Register in `UsageExampleTest`

Add the DSL call next to its category peers in `src/test/kotlin/dev/protsenko/codeguard/usage/UsageExampleTest.kt`.

### 6. Update README.md

Add one bullet under `## Rule Set` in the matching section:
`` - `CodeGuard:<key>`: <what it enforces and why>. Exception: <if any>. ``

### 7. Run `./gradlew codeBaseline`

Must pass clean: tests + detekt + 90% coverage floor.
