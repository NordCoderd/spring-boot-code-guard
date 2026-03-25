package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.core.RuleResult
import dev.protsenko.codeguard.core.springBootRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExcludeRulesTest {

    // scope with field injection violations only — statelessConfiguration passes on it
    private val fieldInjectionScope = Konsist.scopeFromFiles(
        listOf("src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionNegative.kt"),
    )

    private val bothViolationsScope = Konsist.scopeFromFiles(
        listOf(
            "src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionNegative.kt",
            "src/test/kotlin/fixtures/violations/core/configuration/StatelessConfigurationNegative.kt",
        ),
    )

    // ========== verify() ==========

    @Test
    fun `verify() skips excluded rule and passes when remaining rules find no violations`() {
        springBootRules {
            scope = fieldInjectionScope
            general {
                noFieldInjection()
                statelessConfiguration() // passes on fieldInjectionScope
            }
            exclude("CodeGuard:noFieldInjection")
        }.verify()
    }

    @Test
    fun `verify() still enforces non-excluded rules`() {
        val error = assertFailsWith<AssertionError> {
            springBootRules {
                scope = bothViolationsScope
                general {
                    noFieldInjection()
                    statelessConfiguration()
                }
                exclude("CodeGuard:noFieldInjection")
            }.verify()
        }
        assertTrue("CodeGuard:statelessConfiguration" in error.message!!)
        assertTrue("CodeGuard:noFieldInjection" !in error.message!!)
    }

    @Test
    fun `verify() works when exclude is called before rule registration`() {
        springBootRules {
            scope = fieldInjectionScope
            exclude("CodeGuard:noFieldInjection")
            general {
                noFieldInjection()
                statelessConfiguration()
            }
        }.verify()
    }

    @Test
    fun `verify() throws IllegalArgumentException for unknown exclude key listing registered rules`() {
        val error = assertFailsWith<IllegalArgumentException> {
            springBootRules {
                scope = fieldInjectionScope
                general { noFieldInjection() }
                exclude("CodeGuard:doesNotExist")
            }.verify()
        }
        assertTrue("CodeGuard:doesNotExist" in error.message!!)
        assertTrue("CodeGuard:noFieldInjection" in error.message!!)
    }

    @Test
    fun `verify() throws IllegalArgumentException when all rules are excluded`() {
        val error = assertFailsWith<IllegalArgumentException> {
            springBootRules {
                scope = fieldInjectionScope
                general { noFieldInjection() }
                exclude("CodeGuard:noFieldInjection")
            }.verify()
        }
        assertTrue("No rules remaining" in error.message!!)
    }

    @Test
    fun `verify() works with all()`() {
        springBootRules {
            scope = fieldInjectionScope
            all()
            exclude("CodeGuard:noFieldInjection")
        }.verify()
    }

    // ========== verifyWithResults() ==========

    @Test
    fun `verifyWithResults() returns results only for non-excluded rules`() {
        val results = springBootRules {
            scope = bothViolationsScope
            general {
                noFieldInjection()
                statelessConfiguration()
            }
            exclude("CodeGuard:noFieldInjection")
        }.verifyWithResults()

        assertEquals(1, results.size)
        assertTrue(results.single() is RuleResult.Failure)
    }

    @Test
    fun `verifyWithResults() throws IllegalArgumentException for unknown exclude key`() {
        assertFailsWith<IllegalArgumentException> {
            springBootRules {
                scope = fieldInjectionScope
                general { noFieldInjection() }
                exclude("CodeGuard:doesNotExist")
            }.verifyWithResults()
        }
    }

    @Test
    fun `verifyWithResults() throws IllegalArgumentException when all rules are excluded`() {
        assertFailsWith<IllegalArgumentException> {
            springBootRules {
                scope = fieldInjectionScope
                general { noFieldInjection() }
                exclude("CodeGuard:noFieldInjection")
            }.verifyWithResults()
        }
    }
}
