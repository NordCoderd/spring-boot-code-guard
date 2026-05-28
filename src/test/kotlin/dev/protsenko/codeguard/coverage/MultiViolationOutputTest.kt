package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.core.springBootRules
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MultiViolationOutputTest {

    @Test
    fun `multiple violations across rules are all reported before failing`() {
        val error = assertFailsWith<AssertionError> {
            springBootRules {
                scope = Konsist.scopeFromFiles(
                    listOf("src/test/kotlin/fixtures/violations/core/proxy/MultipleViolationsFixture.kt"),
                )
                general {
                    noFieldInjection()
                    noProxyAnnotationsOnPrivateMethods()
                }
            }.verify()
        }
        assertTrue(
            error.message!!.contains("NotificationService"),
            "Expected field injection violation for NotificationService in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("OrderService") || error.message!!.contains("PaymentService"),
            "Expected proxy-on-private violation for OrderService or PaymentService in error: ${error.message}",
        )
    }

    @Test
    fun `multiple violations within single rule are all reported before failing`() {
        val error = assertFailsWith<AssertionError> {
            springBootRules {
                scope = Konsist.scopeFromFiles(
                    listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyMultipleClassViolationFixture.kt"),
                )
                proxy { noSelfInvocationOfProxyMethods() }
            }.verify()
        }
        assertTrue(
            error.message!!.contains("MultiViolationAlphaService"),
            "Expected MultiViolationAlphaService violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("MultiViolationBetaService"),
            "Expected MultiViolationBetaService violation in error: ${error.message}",
        )
    }
}
