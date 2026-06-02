# Service → Web Layer Independence Rule

**Date:** 2026-06-02
**Status:** Approved, ready for implementation

## Goal

Add a Code Guard rule that fails when a Spring `@Service` class depends on the
web layer. Clean layering: services must not know about controllers, HTTP, or
the servlet container. Detection is **import-based**.

## Scope & Decisions

- **Service identification:** `@Service` annotation only (not package, not
  `@Component`). Precise, matches Spring stereotype.
- **Forbidden web-layer imports:**
  - `org.springframework.web.*`
  - `org.springframework.http.*`
  - `jakarta.servlet.*`
  - `javax.servlet.*`
  - In-project classes annotated `@Controller` / `@RestController` (matched by
    fully-qualified name).
- **Detection mechanism:** import statements only. No constructor/property DI
  scanning (injected types are imported anyway).
- **Known trade-off:** `org.springframework.http.*` flags services using
  `ResponseEntity` / `RestClient` / `HttpStatus`. Accepted. Escape hatch is
  per-class `@Suppress("CodeGuard:serviceWebDependency")` or DSL
  `exclude("CodeGuard:serviceWebDependency")`.

## Rule

- **File:** `src/main/kotlin/dev/protsenko/codeguard/rules/web/ServiceUsingRules.kt`
- **Object:** `ServiceUsingRules` (mirrors `ControllerUsingRules`)
- **Rule val:** `serviceWebIndependenceRule`
- `description` = `"Services should not depend on the web layer"`
- `suppressKey` = `"CodeGuard:serviceWebDependency"`

### verify(scope) logic

1. Build `controllerFqns`: fully-qualified names of all scope classes and
   interfaces annotated `@Controller` or `@RestController`.
2. For each `notSuppressedClasses(suppressKey)` annotated `@Service`:
   - Collect `containingFile.imports` whose `name`:
     - starts with `org.springframework.web.`, OR
     - starts with `org.springframework.http.`, OR
     - starts with `jakarta.servlet.`, OR
     - starts with `javax.servlet.`, OR
     - is contained in `controllerFqns`.
   - If any forbidden imports exist, emit a failure.
3. Failure message (single line per service):
   `Service <name> depends on web-layer classes: <comma-joined import names>. Service layer should not depend on the web layer.`
4. If failures non-empty, `throw AssertionError(failures.joinToString("\n"))`.

## Tests & Fixtures (TDD first)

Fixtures: `src/test/kotlin/fixtures/violations/architecture/layer/service/`.
Tests: `ArchitectureRulesViolationTest.kt` (same file as `controllerRepositoryRule`).

### Negative fixtures (must fail)

| Fixture | Triggers | Key import |
|---|---|---|
| `ServiceWebImportNegative.kt` | Spring Web | `org.springframework.web.bind.annotation.RequestParam` |
| `ServiceHttpImportNegative.kt` | Spring HTTP | `org.springframework.http.ResponseEntity` |
| `ServiceServletImportNegative.kt` | Servlet API | `jakarta.servlet.http.HttpServletRequest` |
| `ServiceControllerImportNegative.kt` | in-project controller | imports `...service.SampleRestController` |
| `ServiceWebMultiNegative.kt` | two `@Service` classes, both with web imports | multi-violation `\n`-joined output |

Supporting fixture: `SampleRestController.kt` — `@RestController` class in the
same `service` fixture package, imported by `ServiceControllerImportNegative.kt`.

### Positive fixtures (must pass)

| Fixture | Content |
|---|---|
| `ServiceCleanPositive.kt` | `@Service`, no web imports |
| `ServiceAllowedDepsPositive.kt` | `@Service` importing another service + repository (non-web) |

### Test methods

- `serviceWebIndependenceRule detects Service importing Spring Web`
  → assert exact: `Service <name> depends on web-layer classes: org.springframework.web.bind.annotation.RequestParam. Service layer should not depend on the web layer.`
- `serviceWebIndependenceRule detects Service importing Spring HTTP`
- `serviceWebIndependenceRule detects Service importing Servlet API`
- `serviceWebIndependenceRule detects Service importing in-project controller`
- `serviceWebIndependenceRule detects multiple Service violations` (assert `\n`-joined)
- `serviceWebIndependenceRule passes for clean Service`
- `serviceWebIndependenceRule passes for Service with allowed dependencies`

Each negative asserts the exact error message. Each positive calls
`rule.verify(scope)` with no `assertFailsWith`.

### RED-phase discipline

The rule object must compile so tests run. Create `serviceWebIndependenceRule`
first as a **stub** whose `verify` does nothing (passes everything). Negatives
then fail with "expected AssertionError but none was thrown" — a genuine
behavioral RED, not a compilation error (per AGENTS.md warning). Then implement
real logic → GREEN.

## Registration (after GREEN)

1. `rules/web/WebRuleContext.kt`:
   - Add `serviceWebIndependenceRule` to `allWebRules`.
   - Add DSL function `servicesDoNotDependOnWebLayer()`.
2. `coverage/AllRulesTest.kt`:
   - Add `servicesDoNotDependOnWebLayer()` to the `withIndividual` block.
   - Bump `allWebRules contains N rules` count (current 5 → 6).
3. `usage/UsageExampleTest.kt`: add `servicesDoNotDependOnWebLayer()` to the web
   blocks alongside `controllersDoNotAccessRepositories()`.
4. `README.md`: add bullet under the web section of `## Rule Set`:
   `` - `CodeGuard:serviceWebDependency`: Services must not import web-layer types (Spring Web, Spring HTTP, Servlet API, in-project controllers). Keeps the service layer framework-boundary clean. Exception: per-class `@Suppress`. ``

## Verification

`./gradlew codeBaseline` must pass clean: tests + detekt + 90% coverage floor.
No suppressing violations, no disabling gates.
