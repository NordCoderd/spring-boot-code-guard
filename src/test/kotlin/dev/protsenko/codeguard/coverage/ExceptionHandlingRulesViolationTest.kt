package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.general.customExceptionStructureRule
import dev.protsenko.codeguard.rules.naming.NamingRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for Exception Handling Rules with both positive and negative cases.
 */
class ExceptionHandlingRulesViolationTest {
    @Test
    fun `exceptionHandlerNamingRule detects handler without proper suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/exception/ExceptionHandlerNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.exceptionHandlerNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Exception handler classes should end with 'ExceptionHandler' or 'Advice': BadExceptionManager",
            error.message,
        )
    }

    @Test
    fun `exceptionHandlerNamingRule passes for handlers with proper suffix`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/exception/ExceptionHandlerNamingPositive.kt"),
            )
        NamingRules.exceptionHandlerNamingRule.verify(positiveScope)
    }

    @Test
    fun `customExceptionStructureRule detects exception with wrong parent`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/exception/CustomExceptionStructureNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                customExceptionStructureRule.verify(negativeScope)
            }
        assertEquals(
            "Custom exception classes should extend RuntimeException or proper Spring exceptions: InvalidException extends Throwable, BadCustomException extends Any",
            error.message,
        )
    }

    @Test
    fun `customExceptionStructureRule passes for exceptions with proper parent`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/exception/CustomExceptionStructurePositive.kt"),
            )
        customExceptionStructureRule.verify(positiveScope)
    }

    @Test
    fun `customExceptionStructureRule detects checked exception inheritance`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/exception/CustomExceptionCheckedNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                customExceptionStructureRule.verify(negativeScope)
            }
        assertEquals(
            "Custom exception classes should extend RuntimeException or proper Spring exceptions: CheckedBusinessException extends Exception",
            error.message,
        )
    }
}
