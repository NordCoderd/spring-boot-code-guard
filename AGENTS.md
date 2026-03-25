# Spring Boot Best Practices rules

## About project
This project contains various sets of rules for Spring Boot Best Practices and uses Konsist to validate Kotlin projects.

## Structure
- Each Spring Boot rule has a dedicated test with dedicated fixtures: 
  - [rules](src/main/kotlin/dev/protsenko/codeguard/rules)
  - [fixtures](src/test/kotlin/fixtures/violations)
- Each fixture relied on Spring Boot's methods, classes, annotation, etc. To not have them in the project - they were replicated in the tests' folder.
  - [jakarta](src/test/kotlin/jakarta)
  - [javax](src/test/kotlin/javax)
  - [org](src/test/kotlin/org)
- After the rule is implemented – perform overall checks for codebase: `gradle codeBaseline`

## Adding a new rule

When adding a new rule, update the corresponding `allXxxRules` list in the same file as the rule:

| Category | Rules file | List to update |
|---|---|---|
| General | `rules/general/GeneralRules.kt` | `allCoreRules` |
| JPA | `rules/jpa/JpaRules.kt` | `allJpaRules` |
| Naming | `rules/naming/NamingRules.kt` | `allNamingRules` |
| Packages | `rules/packages/PackageRules.kt` | `allPackageRules` |
| Web | `rules/web/WebRuleContext.kt` | `allWebRules` |

These lists are used by `SpringBootRulesConfiguration.all()` to enable every rule at once. Forgetting to update the list means the rule is silently excluded from `all()`. The per-category count tests in `AllRulesTest` will catch this.