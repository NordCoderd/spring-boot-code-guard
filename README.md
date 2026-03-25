# Spring Code Guard

Set of Spring Boot Best Practices converted into Konsist tests. Helps you write clean and maintainable code and prevent AI slop (at least trying) in your codebase.

Each rule is a static analysis check that runs as a regular JUnit 5 test. Rules inspect your source code structure using [Konsist](https://github.com/LemonAppDev/konsist) — they look at class names, annotations, package locations, and layer dependencies without compiling or running your application. You configure which rules to enforce via a Kotlin DSL, and the test fails with a clear message identifying the exact class or method that violated the rule.

## Installation

There will be installation instructions here.

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

## Rule Set

### General

- `statelessConfigurationRule`: `@Configuration` classes must not declare mutable (`var`) properties (excluding `@Value` and `@ConfigurationProperties` fields) — configuration classes should only define beans and remain side-effect free.
- `beanMethodsInConfigurationRule`: `@Bean` methods must be declared inside `@Configuration` classes; placing them in other classes bypasses Spring's scoping and CGLIB proxy mechanisms.
- `customExceptionStructureRule`: Classes whose name ends with `Exception` must extend `RuntimeException`, a well-known subtype (`IllegalArgumentException`, `IllegalStateException`, `ResponseStatusException`, `NestedRuntimeException`), or another custom exception class — not the raw `Exception` hierarchy.
- `noFieldInjectionRule`: Fields must not be annotated with `@Autowired` or `@Inject`; constructor injection must be used instead to make dependencies explicit and improve testability.
- `loggerInsteadOfPrintRule`: Spring bean classes must not call `println` or `print`; use SLF4J or another structured logging framework so output is observable and controllable in production.

### JPA

- `entityIdRule`: Every `@Entity` class (or one of its parents within the same codebase) must declare a field annotated with `@Id`, as JPA requires a primary key to track entity identity.
- `noDataClassEntityRule`: `@Entity` classes must not be Kotlin `data class` types — the `final` modifier prevents JPA lazy-loading proxies, and structural `equals`/`hashCode` breaks entity identity semantics.
- `transactionalPlacementRule`: `@Transactional` must not appear on `@Controller` or `@RestController` classes or their methods; transaction boundaries belong in the service layer.
- `domainLayerIndependenceRule`: Classes residing in `..domain..` or `..entity..` packages must not import or use any `org.springframework.*` types, keeping the domain model framework-agnostic and portable.

### Naming

- `serviceNamingRule`: Classes annotated with `@Service` must have names ending in `Service`.
- `repositoryNamingRule`: Classes and interfaces annotated with `@Repository` must have names ending in `Repository`.
- `componentNamingRule`: Classes annotated with `@Component` (outside a `..component..` package) must have names ending in `Component`, `Factory`, `Provider`, `Listener`, or `Handler`.
- `controllerNamingRule`: Classes annotated with `@Controller` or `@RestController` must have names ending in `Controller`.
- `exceptionHandlerNamingRule`: Classes annotated with `@RestControllerAdvice` or `@ControllerAdvice` must have names ending in `ExceptionHandler` or `Advice`.

### Packages

- `packageNamingRule`: All package names must be fully lowercase — mixed-case packages break Java/Kotlin conventions and cause issues with tooling.
- `servicePackageRule`: `@Service` classes must reside in a `..service..` package segment.
- `controllerPackageRule`: `@Controller` and `@RestController` classes must reside in a `..controller..` or `..web..` package segment.
- `configurationPackageRule`: `@Configuration` classes must reside in a `..config..` or `..configuration..` package segment.
- `propertiesValidationRule`: `@ConfigurationProperties` classes must reside in a `..property..` package segment.
- `entityPackageRule`: `@Entity` classes must reside in a `..domain..` or `..entity..` package segment.

### Web

- `httpMethodAnnotationRule`: Methods in `@RestController` classes must use specific HTTP method annotations (`@GetMapping`, `@PostMapping`, `@PutMapping`, etc.) instead of the generic `@RequestMapping`, which requires an explicit method attribute to be unambiguous.
- `requestValidationRule`: `@RequestBody` and `@RequestParam` parameters in controller methods must be annotated with `@Valid` or `@Validated` to enforce Bean Validation constraints on incoming data.
- `noTrailingSlashRule`: URL path values in HTTP mapping annotations must not end with a trailing slash — inconsistent paths lead to unexpected 404s and routing ambiguity.
- `restControllerReturnTypeRule`: GET handler methods in `@RestController` classes must not return `Unit`/`void`; every GET endpoint must produce a meaningful response body.
- `dtoSeparationRule`: `@RestController` methods must not accept or return `@Entity` classes directly — use dedicated DTOs to decouple the API contract from the persistence model.
- `controllerRepositoryRule`: `@Controller` and `@RestController` classes must not declare repository types as constructor parameters or properties; controllers should depend only on services, leaving data access to the service layer.
