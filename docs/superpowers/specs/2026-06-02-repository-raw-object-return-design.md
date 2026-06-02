# Repositories Must Not Return Object / Any

**Date:** 2026-06-02
**Status:** Approved, ready for implementation

## Goal

Add a Code Guard rule that fails when a public, declared method of a Spring Data
repository returns a raw `Object` / `Any` type — directly or as a single
collection element (`List<Any>`, `Optional<Object>`, `Page<Any>`). Repositories
should expose concrete, typed results, not erase the type.

## Scope & Decisions

- **Repository identification:** both `@Repository`-annotated classes and Spring
  Data interfaces (`JpaRepository`/`CrudRepository`/`PagingAndSortingRepository`/
  `JpaSpecificationExecutor`). Use the existing `isSpringDataRepository()`
  extension (external-parent resolution + `@Repository`) over
  `notSuppressedClassesAndInterfaces(suppressKey)`.
- **Method scope:** public methods only (`hasPublicOrDefaultModifier`). Only
  *declared* functions are checked — inherited Spring Data methods (e.g.
  `findAll()`) are not scanned, so `JpaRepository<Any, Long>` type arguments do
  not trigger the rule.
- **Match scope:** direct return plus single-type-argument collection nesting.
  `Any`, `Object`, `List<Any>`, `Set<Object>`, `Optional<Any>`, `Page<Any>` are
  flagged. `Map<String, Any>`, `Pair<Any, Long>` and other multi-arg generics are
  NOT flagged (the `Any` there is a value/secondary parameter, common for
  projections).
- **Forbidden simple names:** `Any`, `Object` (covers `kotlin.Any`,
  `java.lang.Object`).
- **Escape hatch:** per-class `@Suppress("CodeGuard:repositoryReturnType")` or DSL
  `exclude("CodeGuard:repositoryReturnType")`.

## Reuse / Refactor

`TypeCandidates.kt` currently lives in `rules/web` (`internal`), used by
`dtoSeparationRule` and `serviceEntityReturnRule`. Move it to
`src/main/kotlin/dev/protsenko/codeguard/core/TypeCandidates.kt` (package `core`)
so all rule packages can share it. Re-point the two web rules with an import.
`normalizeType` and `splitTopLevelTypeArguments` become `internal`.

Add a new matcher to that file:

```kotlin
internal fun isObjectOrAnyType(typeName: String): Boolean {
    val normalized = normalizeType(typeName)
    val genericStart = normalized.indexOf('<')
    if (genericStart < 0 || !normalized.endsWith(">")) {
        return normalized.substringAfterLast(".") in setOf("Any", "Object")
    }
    val args = splitTopLevelTypeArguments(
        normalized.substring(genericStart + 1, normalized.length - 1),
    )
    return args.size == 1 && isObjectOrAnyType(args[0])
}
```

Descends only through single-type-arg generics, so `Map<String, Any>` (2 args)
returns false.

## Rule

- **File:** `src/main/kotlin/dev/protsenko/codeguard/rules/jpa/JpaRules.kt`
  (added to the existing `JpaRules` object).
- **Rule val:** `repositoryReturnTypeRule`
- `description` = `"Repositories should not return Object or Any"`
- `suppressKey` = `"CodeGuard:repositoryReturnType"`

### verify(scope) logic

1. `repositories = scope.notSuppressedClassesAndInterfaces(suppressKey).filter { it.isSpringDataRepository() }`.
2. `repositories.flatMap { it.functions() }.filter { it.hasPublicOrDefaultModifier }`.
3. For each, take `returnType?.name`; if `isObjectOrAnyType(name)`, emit a failure.
4. Failure message:
   `Repository method <Class>.<fn> returns <typeName>. Use a concrete return type instead.`
   (`<Class>` from `function.containingDeclaration`; `<typeName>` is the raw
   `returnType.name`, e.g. `Any` or `List<Any>`.)
5. If failures non-empty, `throw AssertionError(failures.joinToString("\n"))`.

## Tests & Fixtures (TDD first)

Fixtures: `src/test/kotlin/fixtures/violations/jpa/repository/`.
Tests: `JpaRulesViolationTest.kt`.

### Support fixture

- `RepoSampleDto.kt` — plain `class RepoSampleDto`.

### Negative fixtures (must fail)

| Fixture | Declaration |
|---|---|
| `RepositoryReturnsAnyNegative.kt` | interface `: JpaRepository<RepoSampleDto, Long>`, `fun findThing(): Any` |
| `RepositoryReturnsListAnyNegative.kt` | interface `: JpaRepository<RepoSampleDto, Long>`, `fun findThings(): List<Any>` |
| `RepositoryClassReturnsAnyNegative.kt` | `@Repository class ManualRepository`, `fun load(): Any` (class branch) |
| `RepositoryReturnsAnyMultiNegative.kt` | interface with two methods (`Any`, `List<Any>`) → `\n`-joined output |

### Positive fixtures (must pass)

| Fixture | Declaration |
|---|---|
| `RepositoryReturnsConcretePositive.kt` | interface, `fun findByName(name: String): RepoSampleDto`, `fun total(): Long` |
| `RepositoryReturnsMapPositive.kt` | interface, `fun stats(): Map<String, Any>` — proves multi-arg generics are not flagged |
| `RepositoryPrivateAnyPositive.kt` | `@Repository class`, public `fun get(): Long`, private `fun raw(): Any` — proves public-only filter |

### Test methods

Each negative scope includes the relevant fixture(s) (and `RepoSampleDto.kt`
where referenced).

- `repositoryReturnTypeRule detects repository returning Any`
  → assert exact: `Repository method ThingRepository.findThing returns Any. Use a concrete return type instead.`
- `repositoryReturnTypeRule detects repository returning List of Any`
- `repositoryReturnTypeRule detects @Repository class returning Any`
- `repositoryReturnTypeRule detects multiple repository violations` (assert `\n`-joined)
- `repositoryReturnTypeRule passes for repository returning concrete types`
- `repositoryReturnTypeRule passes for repository returning Map`
- `repositoryReturnTypeRule passes for repository with private method returning Any`

Each negative asserts the exact error message. Each positive calls
`rule.verify(scope)` with no `assertFailsWith`.

### RED-phase discipline

Add `repositoryReturnTypeRule` first as a **stub** whose `verify` does nothing.
Negatives then fail with "expected AssertionError but none was thrown" — a
behavioral RED, not a compilation error. Then implement → GREEN.

## Registration (after GREEN)

1. `rules/jpa/JpaRules.kt`: add `repositoryReturnTypeRule` to `allJpaRules`.
2. `rules/jpa/JpaRuleContext.kt`: add DSL function
   `repositoriesDoNotReturnRawObjectTypes()`.
3. `coverage/AllRulesTest.kt`:
   - Add `repositoriesDoNotReturnRawObjectTypes()` to the `withIndividual` jpa
     block.
   - Bump `allJpaRules contains N rules` count (current 4 → 5).
4. `usage/UsageExampleTest.kt`: add `repositoriesDoNotReturnRawObjectTypes()` to
   the jpa blocks.
5. `README.md`: add bullet under the JPA section of `## Rule Set`:
   `` - `CodeGuard:repositoryReturnType`: Public declared methods of Spring Data repositories (`@Repository` classes and `JpaRepository`/`CrudRepository`/etc. interfaces) must not return raw `Object`/`Any`, including as a collection element (`List<Any>`, `Optional<Any>`, `Page<Any>`); return concrete types instead. `Map<String, Any>` and other multi-arg generics are allowed. Exception: per-class `@Suppress`. ``

## Verification

`./gradlew codeBaseline` must pass clean: tests + detekt + 90% coverage floor.
No suppressing violations, no disabling gates.
