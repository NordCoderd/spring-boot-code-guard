# Spring Boot Code Guard

Konsist-based static analysis library for Spring Boot. Rules run as JUnit 5 tests. Kotlin + Gradle (Kotlin DSL), JVM 17. Published to Maven Central as `dev.protsenko:spring-boot-code-guard`.

## Tools (mandatory)

- **File & coding tasks**: use the **mcpc idea** tools (skill: /skill:mcpc-idea-strict-workflow) for every read, edit, create, rename, reformat, inspection, build, and run operation — do not use generic shell fallbacks when an mcpc @idea equivalent exists.
- **Project lookup (when mcpc failed)**: use the **GrepAI** skills (`grepai search`, `grepai trace callers|callees|graph`, `grepai status`) for all code search, symbol discovery, and call-graph questions.

## Layout

```
src/main/kotlin/dev/protsenko/codeguard/
  core/                 # SpringBootRule, DSL entry, suppression, RuleBuilder
  rules/{general,jpa,naming,packages,web}/   # rule objects + DSL contexts
src/test/kotlin/
  dev/protsenko/codeguard/coverage/   # violation tests per rule group
  fixtures/violations/                # fixture classes triggered by tests
```

Entry point: `springBootRules { }.verify()` (`core/SpringBootRulesConfiguration.kt`).

## Rule anatomy

Each rule is an `object : SpringBootRule` with `description`, `suppressKey`, `verify(scope)`. Per-class silence: `@Suppress("CodeGuard:<key>")`. DSL-wide opt-out: `exclude("CodeGuard:<key>")`.

## Adding a new rule — mandatory checklist

Follow every step in order. Do not skip.

### 1. TDD — fixtures and tests first (use `/simplify` skill after)

Use `/skill:mcpc-idea-strict-workflow` to create fixture classes under `src/test/kotlin/fixtures/violations/<category>/<rule>/`:
- `*Negative.kt` — class that triggers the violation (one per interesting failure case)
- `*Positive.kt` — class that must pass (one per interesting pass case)

Then add test methods to the matching `*ViolationTest.kt` in `src/test/kotlin/dev/protsenko/codeguard/coverage/`. Each negative test must assert the exact error message. Each positive test just calls `rule.verify(scope)` without `assertFailsWith`.

Typical cases to cover per rule:
- Wrong stereotype alone in the constrained location → fail
- Correct stereotype alone → pass
- Correct stereotype with file-level helpers in same file → pass
- Unannotated class alone in constrained location → fail

### 2. Implement the rule

Add the rule `object` to the relevant `*Rules.kt` in `src/main/kotlin/.../rules/<category>/`. Use `hasAnnotationWithName(SpringAnnotations.*)` (not `hasAnnotationOf`). Group by `containingFile` when file-level helper exemptions are needed.

### 3. Register in `all*Rules` list

Every `*Rules.kt` file exposes a `val all*Rules: List<SpringBootRule>` at the bottom. Add the new rule there. `AllRulesTest` asserts exact counts — update `allPackageRules contains N rules` (or equivalent) to match.

### 4. Expose in DSL context and `AllRulesTest` individual block

Add a DSL function to the matching `*RuleContext.kt`. Then add the call to the `withIndividual` block in `AllRulesTest.kt` alongside existing peers (e.g. `entitiesInEntityPackage()`).

### 5. Register in `UsageExampleTest`

Add the new DSL call next to its category peers in `src/test/kotlin/dev/protsenko/codeguard/usage/UsageExampleTest.kt`.

### 6. Update README.md

Add one bullet to the matching section in `README.md` under `## Rule Set`. Format: `` - `CodeGuard:<key>`: <what it enforces and why>. Exception: <if any>. ``

### 7. Run `./gradlew codeBaseline`

Must pass clean: tests + detekt + 90% coverage floor.
