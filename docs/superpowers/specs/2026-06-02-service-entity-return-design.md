# Service Layer Must Not Return JPA Entities

**Date:** 2026-06-02
**Status:** Approved, ready for implementation

## Goal

Add a Code Guard rule that fails when a public method of a Spring `@Service`
class returns a JPA `@Entity` type (directly or wrapped in a generic such as
`List`, `Optional`, `Page`). Services should expose DTOs, not leak the
persistence model to their callers.

## Scope & Decisions

- **Service identification:** `@Service` annotation only (consistent with
  `serviceWebDependencyRule`).
- **Method scope:** public methods only. Kotlin default visibility is public, so
  filter on `hasPublicOrDefaultModifier`. private/protected/internal helpers may
  return entities internally — they do not leak across the layer boundary.
- **Trigger:** return types only (the feature is about *returning* entities; not
  parameters).
- **Entity identification:** classes annotated `@Entity` (`jakarta.persistence`
  + `javax.persistence`), matched by simple name within the scanned scope — same
  approach as `dtoSeparationRule`.
- **Generic unwrapping:** reuse the existing type-candidate extraction so
  `List<Entity>`, `Optional<Entity>`, `Page<Entity>`, `Set<Entity>`, etc. are all
  caught.
- **Escape hatch:** per-class `@Suppress("CodeGuard:serviceEntityReturn")` or DSL
  `exclude("CodeGuard:serviceEntityReturn")`.

## Reuse / Refactor

The helpers `extractTypeCandidates`, `genericWrappers`, `normalizeType`, and
`splitTopLevelTypeArguments` currently live as `private` members of
`ResponseHandlingRules`. Extract them into a package-internal file
`src/main/kotlin/dev/protsenko/codeguard/rules/web/TypeCandidates.kt` as
`internal` functions/values. Both `dtoSeparationRule` and the new rule call them.
`dtoSeparationRule` behavior is unchanged — it only sources the helpers from the
new file.

## Rule

- **File:** `src/main/kotlin/dev/protsenko/codeguard/rules/web/ServiceUsingRules.kt`
  (added to the existing `ServiceUsingRules` object).
- **Rule val:** `serviceEntityReturnRule`
- `description` = `"Services should not return JPA entities"`
- `suppressKey` = `"CodeGuard:serviceEntityReturn"`

### verify(scope) logic

1. `entityClasses` =
   `notSuppressedClasses(suppressKey).withAnnotationNamed(entityAnnotations).map { it.name }.toSet()`.
2. For each `notSuppressedClasses(suppressKey)` annotated `@Service`:
   - `functions().filter { it.hasPublicOrDefaultModifier }`
   - for each, take `returnType?.name`, run `extractTypeCandidates`, find the
     first candidate contained in `entityClasses`.
   - on match, emit a failure.
3. Failure message:
   `Service method <Class>.<fn> returns JPA entity <Name>. Use a DTO instead.`
   (`<Class>` from `function.containingDeclaration`, which renders the simple
   class name.)
4. If failures non-empty, `throw AssertionError(failures.joinToString("\n"))`.

## Tests & Fixtures (TDD first)

Fixtures: `src/test/kotlin/fixtures/violations/architecture/layer/service/`.
Tests: `ArchitectureRulesViolationTest.kt`.

### Support fixtures

- `ServiceEntitySample.kt` — `@Entity class SampleEntity` (jakarta).
- `ServiceDtoSample.kt` — plain `SampleDto`.

### Negative fixtures (must fail)

| Fixture | Service method |
|---|---|
| `ServiceReturnsEntityNegative.kt` | `@Service`, public `fun find(): SampleEntity` |
| `ServiceReturnsEntityListNegative.kt` | `@Service`, public `fun all(): List<SampleEntity>` |
| `ServiceReturnsEntityMultiNegative.kt` | two `@Service` classes, each returns an entity (`\n`-joined output) |

### Positive fixtures (must pass)

| Fixture | Content |
|---|---|
| `ServiceReturnsDtoPositive.kt` | `@Service` returning `SampleDto` and primitives |
| `ServicePrivateEntityPositive.kt` | `@Service` with a **private** method returning `SampleEntity` and a public method returning a DTO — proves the public-only filter |

### Test methods

Each negative scope also includes `ServiceEntitySample.kt`.

- `serviceEntityReturnRule detects Service returning entity`
  → assert exact: `Service method EntityReturningService.find returns JPA entity SampleEntity. Use a DTO instead.`
- `serviceEntityReturnRule detects entity wrapped in List`
- `serviceEntityReturnRule detects multiple Service violations` (assert `\n`-joined)
- `serviceEntityReturnRule passes for Service returning DTO`
- `serviceEntityReturnRule passes for Service with private method returning entity`

Each negative asserts the exact error message. Each positive calls
`rule.verify(scope)` with no `assertFailsWith`.

### RED-phase discipline

Add `serviceEntityReturnRule` first as a **stub** whose `verify` does nothing.
Negatives then fail with "expected AssertionError but none was thrown" — a
behavioral RED, not a compilation error. Then implement → GREEN.

## Registration (after GREEN)

1. `rules/web/WebRuleContext.kt`:
   - Add `serviceEntityReturnRule` to `allWebRules`.
   - Add DSL function `servicesDoNotReturnEntities()`.
2. `coverage/AllRulesTest.kt`:
   - Add `servicesDoNotReturnEntities()` to the `withIndividual` block.
   - Bump `allWebRules contains N rules` count (current 6 → 7).
3. `usage/UsageExampleTest.kt`: add `servicesDoNotReturnEntities()` to the web
   blocks.
4. `README.md`: add bullet under the Web section of `## Rule Set`:
   `` - `CodeGuard:serviceEntityReturn`: Public methods of `@Service` classes must not return JPA `@Entity` types (including inside `List`/`Optional`/`Page`/etc.); return DTOs so the persistence model does not leak across the service boundary. Exception: per-class `@Suppress`. ``

## Verification

`./gradlew codeBaseline` must pass clean: tests + detekt + 90% coverage floor.
No suppressing violations, no disabling gates.
