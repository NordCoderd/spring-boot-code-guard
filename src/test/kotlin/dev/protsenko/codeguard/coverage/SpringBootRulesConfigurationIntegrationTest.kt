package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import dev.protsenko.codeguard.core.RuleResult
import dev.protsenko.codeguard.core.SpringBootRulesConfiguration
import dev.protsenko.codeguard.core.springBootRules
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for SpringBootRulesConfiguration.
 *
 * Verifies that the DSL wires all context blocks, that verify() / verifyWithResults()
 * work end-to-end, and that rules detect violations through the DSL pipeline.
 */
class SpringBootRulesConfigurationIntegrationTest {

    // ---- fixture scope ----

    /**
     * Scope covering all files under fixtures/violations, discovered dynamically via Konsist.
     *
     * Including both positive and negative fixtures is intentional: rules fire on any violation
     * found in the scope, so positive files do not mask violations in negative files.
     * Support files (e.g. User.kt for dtoSeparation, UserRepository.kt for controllerRepository)
     * are included automatically without special-casing.
     */
    private val fixtureScope: KoScope by lazy {
        val root = File(System.getProperty("user.dir"))
        val paths =
            File(root, "src/test/kotlin/fixtures/violations")
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .map { it.toRelativeString(root) }
                .toList()
        Konsist.scopeFromFiles(paths)
    }

    // ---- DSL helper ----

    private fun SpringBootRulesConfiguration.configureAllRules() {
        general {
            noFieldInjection()
            statelessConfiguration()
            beanMethodsInConfiguration()
            customExceptionStructure()
            useLoggerNotPrintln()
        }
        web {
            restControllerReturnTypes()
            properHttpMethodAnnotations()
            noTrailingSlashesInPaths()
            separateDtosFromEntities()
            validateRequestBody()
            controllersDoNotAccessRepositories()
        }
        jpa {
            entitiesHaveIdField()
            noDataClassEntities()
            transactionalOnServiceLayer()
            domainLayerNoDependencies()
        }
        naming {
            serviceNamingConvention()
            componentNamingConvention()
            controllerNamingConvention()
            repositoryNamingConvention()
            exceptionHandlerNaming()
        }
        packages {
            packageNamingConvention()
            serviceInServicePackage()
            controllerInControllerPackage()
            configurationInConfigPackage()
            configurationPropertiesInPropertiesPackage()
            entitiesInEntityPackage()
        }
    }

    // ========== Rule wiring ==========

    @Test
    fun `all DSL context blocks register all 26 rules`() {
        val config =
            springBootRules {
                scope = Konsist.scopeFromFiles(emptyList())
                configureAllRules()
            }

        assertEquals(26, config.getAllRules().size)
    }

    // ========== verify() ==========

    @Test
    fun `verify() passes for all rules when scope contains no files`() {
        springBootRules {
            scope = Konsist.scopeFromFiles(emptyList())
            configureAllRules()
        }.verify()
    }

    @Test
    fun `verify() reports all violations from all failing rules in a single error`() {
        val error =
            assertFailsWith<AssertionError> {
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
                }.verify()
            }

        assertTrue(
            error.message!!.contains("@Autowired") || error.message!!.contains("field"),
            "Expected field injection violation in message",
        )
        assertTrue(
            error.message!!.contains("mutable") || error.message!!.contains("StatefulConfiguration"),
            "Expected stateless configuration violation in message",
        )
    }

    // ========== verifyWithResults() ==========

    @Test
    fun `verifyWithResults() returns one result per configured rule`() {
        val config =
            springBootRules {
                scope = fixtureScope
                configureAllRules()
            }

        val verifyResults = config.verifyWithResults()
        assertEquals(26, verifyResults.size)

        val unexpectedSuccesses =
            config
                .getAllRules()
                .zip(verifyResults)
                .filter { (_, result) -> result is RuleResult.Success }
                .map { (rule, _) -> rule.description }

        assertTrue(
            unexpectedSuccesses.isEmpty(),
            "Expected all rules to fail but these passed: $unexpectedSuccesses",
        )
    }

    @Test
    fun `verifyWithResults() reports failures for fixture corpus`() {
        val config =
            springBootRules {
                scope = fixtureScope
                general {
                    noFieldInjection()
                    statelessConfiguration()
                    beanMethodsInConfiguration()
                    useLoggerNotPrintln()
                }
                web {
                    restControllerReturnTypes()
                    properHttpMethodAnnotations()
                    noTrailingSlashesInPaths()
                }
                jpa {
                    entitiesHaveIdField()
                    noDataClassEntities()
                    transactionalOnServiceLayer()
                    domainLayerNoDependencies()
                }
            }

        val results = config.verifyWithResults()
        assertTrue(
            results.any { it is RuleResult.Failure },
            "Expected at least one rule to detect a violation in the fixture corpus",
        )
    }

    @Test
    fun `verifyWithResults() returns Success for a rule that passes against its own positive fixture`() {
        val config =
            springBootRules {
                scope =
                    Konsist.scopeFromFiles(
                        listOf("src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionPositive.kt"),
                    )
                general {
                    noFieldInjection()
                }
            }

        assertEquals(RuleResult.Success, config.verifyWithResults().single())
    }

    @Test
    fun `verifyWithResults() Failure entries carry a non-blank message and non-empty violations`() {
        val config =
            springBootRules {
                scope =
                    Konsist.scopeFromFiles(
                        listOf("src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionNegative.kt"),
                    )
                general {
                    noFieldInjection()
                }
            }

        val results = config.verifyWithResults()
        val failure = results.single() as RuleResult.Failure

        assertTrue(failure.message.isNotBlank())
        assertTrue(failure.violations.isNotEmpty())
    }
}
