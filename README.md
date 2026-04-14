# Spring Boot Code Guard

Set of Spring Boot Best Practices converted into Konsist tests. Helps you write clean and maintainable code and prevent AI slop (at least trying) in your codebase.

Each rule is a static analysis check that runs as a regular JUnit 5 test. Rules inspect your source code structure using [Konsist](https://github.com/LemonAppDev/konsist) — they look at class names, annotations, package locations, and layer dependencies without compiling or running your application. You configure which rules to enforce via a Kotlin DSL, and the test fails with a clear message identifying the exact class or method that violated the rule.

## Installation

Maven Central: https://central.sonatype.com/artifact/dev.protsenko/spring-boot-code-guard/overview

Add dependency to your `build.gradle.kts` or `build.gradle`:

```kotlin
implementation("dev.protsenko:spring-boot-code-guard:1.0.4")
```

## Usage

Add a Konsist test file to your project (e.g. `src/test/kotlin/SpringCodeGuardTest.kt`) and call `springBootRules { }.verify()` inside a JUnit 5 test.

### All rules

Enable every available rule with a single call:

```kotlin
@Test
fun `spring boot rules`() {
    springBootRules {
        all()
    }.verify()
}
```

### Selected rules

Pick only the rules relevant to your project:

```kotlin
@Test
fun `spring boot rules`() {
    springBootRules {
        general {
            noFieldInjection()
            // ... and other rules
        }
        jpa {
            entitiesHaveIdField()
            // ... and other rules
        }
        naming {
            serviceNamingConvention()
            // ... and other rules
        }
        packages {
            packageNamingConvention()
            // ... and other rules
        }
        web {
            properHttpMethodAnnotations()
            // ... and other rules
        }
    }.verify()
}
```

## Suppressing Rules

When a rule fires, the violation message is prefixed with its rule ID:

```
CodeGuard:noFieldInjection: Found 1 field(s) with @Autowired/@Inject in: LegacyService. Use constructor injection instead.
```

To suppress a rule for a specific class, annotate it with `@Suppress` using that ID:

```kotlin
@Suppress("CodeGuard:noFieldInjection")
@Service
class LegacyService {
    @Autowired
    lateinit var repo: UserRepository
}
```

Suppression is class-level — it silences all checks from the given rule for that class and its members.

## Excluding Rules

To disable a rule entirely for the whole test, use `exclude()` inside the DSL block with the rule's suppress key:

```kotlin
@Test
fun `spring boot rules`() {
    springBootRules {
        all()
        exclude("CodeGuard:noFieldInjection")
    }.verify()
}
```

Multiple rules can be excluded at once:

```kotlin
springBootRules {
    all()
    exclude(
        "CodeGuard:noFieldInjection",
        "CodeGuard:transactionalPlacement",
    )
}.verify()
```

`exclude()` can be placed anywhere in the block — before or after rule registration. Exclusions are applied at `verify()` time regardless of order.

Passing an unknown key throws an error listing all registered rule keys. Excluding every registered rule also throws — at least one rule must remain active.

## Rule Set

### General

- `CodeGuard:statelessConfiguration`: `@Configuration` classes must not declare mutable (`var`) properties (excluding `@Value` and `@ConfigurationProperties` fields) — configuration classes should only define beans and remain side-effect free.
- `CodeGuard:beanMethodsInConfiguration`: `@Bean` methods must be declared inside `@Configuration` classes; placing them in other classes bypasses Spring's scoping and CGLIB proxy mechanisms.
- `CodeGuard:customExceptionStructure`: Classes whose name ends with `Exception` must extend `RuntimeException`, a well-known subtype (`IllegalArgumentException`, `IllegalStateException`, `ResponseStatusException`, `NestedRuntimeException`), or another custom exception class — not the raw `Exception` hierarchy.
- `CodeGuard:noFieldInjection`: Fields must not be annotated with `@Autowired` or `@Inject`; constructor injection must be used instead to make dependencies explicit and improve testability.
- `CodeGuard:loggerInsteadOfPrint`: Spring bean classes must not call `println` or `print`; use SLF4J or another structured logging framework so output is observable and controllable in production.
- `CodeGuard:noStackTracePrint`: Spring bean classes must not call `printStackTrace()` directly; use structured logging so stack traces are captured by the application logging pipeline.
- `CodeGuard:noProxyAnnotationsOnPrivateMethods`: `@Transactional`, `@Cacheable`, `@CacheEvict`, `@CachePut`, and `@Async` must not be placed on `private` methods — Spring proxy cannot intercept private methods, so the annotation is silently ignored.

### JPA

- `CodeGuard:entityId`: Every `@Entity` class (or one of its parents within the same codebase) must declare a field annotated with `@Id`, as JPA requires a primary key to track entity identity.
- `CodeGuard:noDataClassEntity`: `@Entity` classes must not be Kotlin `data class` types — the `final` modifier prevents JPA lazy-loading proxies, and structural `equals`/`hashCode` breaks entity identity semantics.
- `CodeGuard:transactionalPlacement`: `@Transactional` must not appear on `@Controller` or `@RestController` classes or their methods; transaction boundaries belong in the service layer.
- `CodeGuard:domainLayerIndependence`: Classes residing in `..domain..` or `..entity..` packages must not import or use any `org.springframework.*` types, keeping the domain model framework-agnostic and portable.

### Naming

- `CodeGuard:serviceNaming`: Classes annotated with `@Service` must have names ending in `Service`.
- `CodeGuard:repositoryNaming`: Classes and interfaces annotated with `@Repository` must have names ending in `Repository`.
- `CodeGuard:controllerNaming`: Classes annotated with `@Controller` or `@RestController` must have names ending in `Controller`.
- `CodeGuard:exceptionHandlerNaming`: Classes annotated with `@RestControllerAdvice` or `@ControllerAdvice` must have names ending in `ExceptionHandler` or `Advice`.
- `CodeGuard:configurationPropertiesNaming`: Classes annotated with `@ConfigurationProperties` must have names ending in `Properties`.

### Packages

- `CodeGuard:packageNaming`: All package names must be fully lowercase — mixed-case packages break Java/Kotlin conventions and cause issues with tooling.
- `CodeGuard:servicePackage`: `@Service` classes must reside in a `..service..` package segment.
- `CodeGuard:controllerPackage`: `@Controller` and `@RestController` classes must reside in a `..controller..` or `..web..` package segment.
- `CodeGuard:configurationPackage`: `@Configuration` classes must reside in a `..config..` or `..configuration..` package segment.
- `CodeGuard:propertiesValidation`: `@ConfigurationProperties` classes must reside in a `..property..` package segment.
- `CodeGuard:configurationPropertiesPrefixKebabCase`: `@ConfigurationProperties` prefixes must use lowercase kebab-case segments separated by dots, such as `app.mail` or `app-mail.client`.
- `CodeGuard:entityPackage`: `@Entity` classes must reside in a `..domain..` or `..entity..` package segment.

### Web

- `CodeGuard:httpMethodAnnotation`: Methods in `@RestController` classes must use specific HTTP method annotations (`@GetMapping`, `@PostMapping`, `@PutMapping`, etc.) instead of the generic `@RequestMapping`, which requires an explicit method attribute to be unambiguous.
- `CodeGuard:noTrailingSlash`: URL path values in HTTP mapping annotations must not end with a trailing slash — inconsistent paths lead to unexpected 404s and routing ambiguity.
- `CodeGuard:restControllerReturnType`: GET handler methods in `@RestController` classes must not return `Unit`/`void`; every GET endpoint must produce a meaningful response body.
- `CodeGuard:dtoSeparation`: `@RestController` methods must not accept or return `@Entity` classes directly — use dedicated DTOs to decouple the API contract from the persistence model.
- `CodeGuard:controllerRepository`: `@Controller` and `@RestController` classes must not declare repository types as constructor parameters or properties; controllers should depend only on services, leaving data access to the service layer.
