package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.jpa.JpaRules
import dev.protsenko.codeguard.rules.web.ControllerUsingRules
import dev.protsenko.codeguard.rules.web.ServiceUsingRules
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

    // ========== ServiceUsingRules Tests ==========

    @Test
    fun `serviceWebIndependenceRule detects Service importing Spring Web`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceWebImportNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceWebIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Service WebImportService depends on web-layer classes: " +
                "org.springframework.web.bind.annotation.RequestParam. " +
                "Service layer should not depend on the web layer.",
            error.message,
        )
    }

    @Test
    fun `serviceWebIndependenceRule detects Service importing Spring HTTP`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceHttpImportNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceWebIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Service HttpImportService depends on web-layer classes: " +
                "org.springframework.http.ResponseEntity. " +
                "Service layer should not depend on the web layer.",
            error.message,
        )
    }

    @Test
    fun `serviceWebIndependenceRule detects Service importing Servlet API`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceServletImportNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceWebIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Service ServletImportService depends on web-layer classes: " +
                "jakarta.servlet.http.HttpServletRequest. " +
                "Service layer should not depend on the web layer.",
            error.message,
        )
    }

    @Test
    fun `serviceWebIndependenceRule detects Service importing in-project controller`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceControllerImportNegative.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/web/SampleRestController.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceWebIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Service ControllerImportService depends on web-layer classes: " +
                "fixtures.violations.architecture.layer.web.SampleRestController. " +
                "Service layer should not depend on the web layer.",
            error.message,
        )
    }

    @Test
    fun `serviceWebIndependenceRule detects multiple Service violations`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceWebMultiNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceWebIndependenceRule.verify(negativeScope)
            }
        assertEquals(
            "Service MultiServiceAlpha depends on web-layer classes: " +
                "org.springframework.http.ResponseEntity. " +
                "Service layer should not depend on the web layer.\n" +
                "Service MultiServiceBeta depends on web-layer classes: " +
                "org.springframework.http.ResponseEntity. " +
                "Service layer should not depend on the web layer.",
            error.message,
        )
    }

    @Test
    fun `serviceWebIndependenceRule passes for clean Service`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceCleanPositive.kt"),
            )
        ServiceUsingRules.serviceWebIndependenceRule.verify(positiveScope)
    }

    @Test
    fun `serviceWebIndependenceRule passes for Service with allowed dependencies`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceAllowedDepsPositive.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/repository/UserRepository.kt",
                ),
            )
        ServiceUsingRules.serviceWebIndependenceRule.verify(positiveScope)
    }

    @Test
    fun `serviceEntityReturnRule detects Service returning entity`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceReturnsEntityNegative.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceEntitySample.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceEntityReturnRule.verify(negativeScope)
            }
        assertEquals(
            "Service method EntityReturningService.find returns JPA entity SampleEntity. Use a DTO instead.",
            error.message,
        )
    }

    @Test
    fun `serviceEntityReturnRule detects entity wrapped in List`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceReturnsEntityListNegative.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceEntitySample.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceEntityReturnRule.verify(negativeScope)
            }
        assertEquals(
            "Service method EntityListReturningService.all returns JPA entity SampleEntity. Use a DTO instead.",
            error.message,
        )
    }

    @Test
    fun `serviceEntityReturnRule detects multiple Service violations`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceReturnsEntityMultiNegative.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceEntitySample.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ServiceUsingRules.serviceEntityReturnRule.verify(negativeScope)
            }
        assertEquals(
            "Service method MultiEntityServiceAlpha.find returns JPA entity SampleEntity. Use a DTO instead.\n" +
                "Service method MultiEntityServiceBeta.find returns JPA entity SampleEntity. Use a DTO instead.",
            error.message,
        )
    }

    @Test
    fun `serviceEntityReturnRule passes for Service returning DTO`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceReturnsDtoPositive.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceDtoSample.kt",
                ),
            )
        ServiceUsingRules.serviceEntityReturnRule.verify(positiveScope)
    }

    @Test
    fun `serviceEntityReturnRule passes for Service with private method returning entity`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServicePrivateEntityPositive.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceEntitySample.kt",
                    "src/test/kotlin/fixtures/violations/architecture/layer/service/ServiceDtoSample.kt",
                ),
            )
        ServiceUsingRules.serviceEntityReturnRule.verify(positiveScope)
    }
}
