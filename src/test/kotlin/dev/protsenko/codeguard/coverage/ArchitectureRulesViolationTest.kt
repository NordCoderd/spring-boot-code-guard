package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.jpa.JpaRules
import dev.protsenko.codeguard.rules.web.ControllerUsingRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for ArchitectureRules
 * with both positive and negative cases and error message assertions.
 */
class ArchitectureRulesViolationTest {
    // ========== ArchitectureRules Tests ==========

    @Test
    fun `controllerRepositoryRule detects Controller depending on Repository`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/ControllerRepositoryNegative.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/repository/UserRepository.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ControllerUsingRules.controllerRepositoryRule.verify(negativeScope)
            }
        assertEquals(
            "Controller BadController directly depends on repository UserRepository. Controllers should only depend on services.",
            error.message,
        )
    }

    @Test
    fun `controllerRepositoryRule passes for Controller depending on Service`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/ControllerRepositoryPositive.kt"),
            )
        ControllerUsingRules.controllerRepositoryRule.verify(positiveScope)
    }

    @Test
    fun `controllerRepositoryRule detects Controller depending on Repository interface`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/ControllerRepositoryInterfaceNegative.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/repository/UserJpaRepository.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ControllerUsingRules.controllerRepositoryRule.verify(negativeScope)
            }
        assertEquals(
            "Controller BadInterfaceController directly depends on repository UserJpaRepository. Controllers should only depend on services.",
            error.message,
        )
    }

    @Test
    fun `domainLayerIndependenceRule detects Spring imports in domain`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/DomainLayerIndependenceNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                JpaRules.domainLayerIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Domain class DomainEntity has Spring annotations: Component. Domain layer should be framework-independent.",
            error.message,
        )
    }

    @Test
    fun `domainLayerIndependenceRule passes for clean domain layer`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/DomainLayerIndependencePositive.kt"),
            )
        JpaRules.domainLayerIndependenceRule.verify(positiveScope)
    }

    @Test
    fun `domainLayerIndependenceRule detects Spring imports in entity package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/EntityLayerIndependenceNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                JpaRules.domainLayerIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Domain class EntityDomainObject has Spring annotations: Component. Domain layer should be framework-independent.",
            error.message,
        )
    }

    @Test
    fun `domainLayerIndependenceRule passes for clean entity package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/EntityLayerIndependencePositive.kt"),
            )
        JpaRules.domainLayerIndependenceRule.verify(positiveScope)
    }
}
