package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.general.CoreRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for Observability Rules with both positive and negative cases.
 */
class ObservabilityRulesViolationTest {
    @Test
    fun `core observability rules no longer expose loggerFieldRule`() {
        val hasLoggerFieldRule =
            CoreRules::class.java.declaredFields
                .any { it.name == "loggerFieldRule" }

        kotlin.test.assertFalse(hasLoggerFieldRule)
    }

    @Test
    fun `loggerInsteadOfPrintRule detects println usage`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/observability/LoggerInsteadOfPrintNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                CoreRules.loggerInsteadOfPrintRule.verify(negativeScope)
            }
        assertEquals(
            "PrintlnService uses println/print in method(s): processData. Use proper logging (SLF4J) instead of console output.",
            error.message,
        )
    }

    @Test
    fun `loggerInsteadOfPrintRule passes for proper logging`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/observability/LoggerInsteadOfPrintPositive.kt"),
            )
        CoreRules.loggerInsteadOfPrintRule.verify(positiveScope)
    }

    @Test
    fun `loggerInsteadOfPrintRule does not flag method names containing print token`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/observability/LoggerInsteadOfPrintNameContainsPrintPositive.kt"),
            )
        CoreRules.loggerInsteadOfPrintRule.verify(positiveScope)
    }
}
