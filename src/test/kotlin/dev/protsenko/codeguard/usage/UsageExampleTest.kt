package dev.protsenko.codeguard.usage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.core.RuleResult
import dev.protsenko.codeguard.core.springBootRules
import org.junit.jupiter.api.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Demonstrates common usage patterns for Spring Code Guard.
 * These examples are referenced in the README Usage section.
 *
 * In a real Spring Boot project, remove the fixture scope override.
 * In a multi-module build, set moduleName to verify one module's production sources.
 */
class UsageExampleTest {
    // -------------------------------------------------------------------------
    // Example 0: All rules at once
    // -------------------------------------------------------------------------

    @Test
    fun `example - enable all rules with a single call`() {
        springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            all()
        }.verify()
    }

    // -------------------------------------------------------------------------
    // Example 1: Minimal setup — enforce a single rule
    // -------------------------------------------------------------------------

    @Test
    fun `example - minimal setup enforcing no field injection`() {
        // In your project: remove the scope line, or set moduleName = "my-module" in a multi-module build
        springBootRules {
            scope =
                Konsist.scopeFromFiles(
                    listOf("src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionPositive.kt"),
                )
            general {
                noFieldInjection()
            }
        }.verify()
    }

    // -------------------------------------------------------------------------
    // Example 2: Full project validation — all rules
    // -------------------------------------------------------------------------

    @Test
    fun `example - full Spring Boot project validation`() {
        // In your project: omit the scope line, or set moduleName = "my-module" in a multi-module build
        springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            general {
                noFieldInjection()
                statelessConfiguration()
                beanMethodsInConfiguration()
                customExceptionStructure()
                useLoggerNotPrintln()
                noStackTracePrint()
            }
            proxy {
                noSelfInvocationOfProxyMethods()
            }
            jpa {
                entitiesHaveIdField()
                noDataClassEntities()
                transactionalOnServiceLayer()
                domainLayerNoDependencies()
            }
            naming {
                serviceNamingConvention()
                repositoryNamingConvention()
                controllerNamingConvention()
                exceptionHandlerNaming()
                configurationPropertiesNaming()
            }
            packages {
                packageNamingConvention()
                serviceInServicePackage()
                controllerInControllerPackage()
                configurationInConfigPackage()
                configurationPropertiesInPropertiesPackage()
                configurationPropertiesPrefixKebabCase()
                entitiesInEntityPackage()
                onlyServicesInServicePackage()
                onlyEntitiesInEntityPackage()
                onlyControllersInControllerPackage()
                onlyConfigurationsInConfigPackage()
                onlyPropertiesInPropertyPackage()
                repositoryInRepositoryPackage()
                onlyRepositoriesInRepositoryPackage()
            }
            web {
                properHttpMethodAnnotations()
                noTrailingSlashesInPaths()
                restControllerReturnTypes()
                separateDtosFromEntities()
                controllersDoNotAccessRepositories()
            }
        }.verify()
    }

    // -------------------------------------------------------------------------
    // Example 3: REST-only project (no JPA) — only web and naming rules
    // -------------------------------------------------------------------------

    @Test
    fun `example - REST-only project without JPA rules`() {
        springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            general {
                noFieldInjection()
                useLoggerNotPrintln()
                noStackTracePrint()
            }
            naming {
                controllerNamingConvention()
                serviceNamingConvention()
            }
            web {
                properHttpMethodAnnotations()
                noTrailingSlashesInPaths()
                restControllerReturnTypes()
            }
        }.verify()
    }

    // -------------------------------------------------------------------------
    // Example 4: Soft verification — collect results without failing the build
    // -------------------------------------------------------------------------

    @Test
    fun `example - collect violations without failing the build`() {
        val results =
            springBootRules {
                scope =
                    Konsist.scopeFromFiles(
                        listOf(
                            "src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionNegative.kt",
                            "src/test/kotlin/fixtures/violations/core/configuration/StatelessConfigurationNegative.kt",
                        ),
                    )
                general {
                    noFieldInjection()
                    statelessConfiguration()
                }
            }.verifyWithResults()

        val failures = results.filterIsInstance<RuleResult.Failure>()

        // Report violations without crashing — useful for gradual adoption
        failures.forEach { failure ->
            println("Rule violation: ${failure.message}")
        }

        assertTrue(failures.isNotEmpty(), "Expected violations to be detected in negative fixtures")
    }

    // -------------------------------------------------------------------------
    // Example 5: Scoped to a specific module
    // -------------------------------------------------------------------------

    @Test
    fun `example - verify only a specific module or set of files`() {
        // In your project: use moduleName = "my-module" to target production sources in one Gradle or Maven module.
        // Use scope only when you need a custom Konsist scope.
        val result =
            springBootRules {
                scope =
                    Konsist.scopeFromFiles(
                        listOf(
                            "src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionPositive.kt",
                            "src/test/kotlin/fixtures/violations/core/configuration/StatelessConfigurationPositive.kt",
                        ),
                    )
                general {
                    noFieldInjection()
                    statelessConfiguration()
                }
            }.verifyWithResults()

        result.forEach { assertIs<RuleResult.Success>(it) }
    }

    // -------------------------------------------------------------------------
    // Example 6: Architecture enforcement — clean layering for DDD
    // -------------------------------------------------------------------------

    @Test
    fun `example - enforce clean architecture layering for DDD project`() {
        springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            jpa {
                domainLayerNoDependencies()
                transactionalOnServiceLayer()
            }
            web {
                controllersDoNotAccessRepositories()
                separateDtosFromEntities()
            }
            packages {
                serviceInServicePackage()
                controllerInControllerPackage()
                entitiesInEntityPackage()
                onlyServicesInServicePackage()
                onlyEntitiesInEntityPackage()
                onlyControllersInControllerPackage()
                onlyConfigurationsInConfigPackage()
                onlyPropertiesInPropertyPackage()
                repositoryInRepositoryPackage()
                onlyRepositoriesInRepositoryPackage()
            }
        }.verify()
    }
}
