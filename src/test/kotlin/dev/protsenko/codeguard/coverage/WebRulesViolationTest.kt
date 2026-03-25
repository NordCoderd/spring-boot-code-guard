package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.jpa.JpaRules
import dev.protsenko.codeguard.rules.naming.NamingRules
import dev.protsenko.codeguard.rules.packages.PackageRules
import dev.protsenko.codeguard.rules.web.RequestHandlingRules
import dev.protsenko.codeguard.rules.web.ResponseHandlingRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for ControllerRules, RequestMappingRules, and ResponseHandlingRules
 * with both positive and negative cases and error message assertions.
 */
class WebRulesViolationTest {
    // ========== ControllerRules Tests ==========

    @Test
    fun `controllerNamingRule detects Controller without Controller suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/ControllerNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.controllerNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Controller classes should end with 'Controller': BadApi",
            error.message,
        )
    }

    @Test
    fun `controllerNamingRule passes for Controller with Controller suffix`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/ControllerNamingPositive.kt"),
            )
        NamingRules.controllerNamingRule.verify(positiveScope)
    }

    @Test
    fun `controllerPackageRule detects Controller not in controller or web package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/ControllerPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.controllerPackageRule.verify(negativeScope)
            }
        assertEquals(
            "Controllers should be in .controller or .web package: NotInControllerPackageController (fixtures.violations)",
            error.message,
        )
    }

    @Test
    fun `controllerPackageRule passes for Controller in controller package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/ControllerPackagePositive.kt"),
            )
        PackageRules.controllerPackageRule.verify(positiveScope)
    }

    @Test
    fun `restControllerReturnTypeRule detects void returning GET methods`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/RestControllerReturnTypeNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                ResponseHandlingRules.restControllerReturnTypeRule.verify(negativeScope)
            }
        assertEquals(
            "GET methods in @RestController should return a value: VoidReturningController.voidMethod",
            error.message,
        )
    }

    @Test
    fun `restControllerReturnTypeRule passes for GET methods with return types`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/controller/RestControllerReturnTypePositive.kt"),
            )
        ResponseHandlingRules.restControllerReturnTypeRule.verify(positiveScope)
    }

    // ========== RequestMappingRules Tests ==========

    @Test
    fun `httpMethodAnnotationRule detects RequestMapping usage`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/requestmapping/HttpMethodAnnotationNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                RequestHandlingRules.httpMethodAnnotationRule.verify(negativeScope)
            }
        assertEquals(
            "Use specific HTTP method annotations (@GetMapping, @PostMapping, etc.) instead of @RequestMapping: GenericRequestMappingController.endpoint",
            error.message,
        )
    }

    @Test
    fun `httpMethodAnnotationRule passes for specific HTTP method annotations`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/requestmapping/HttpMethodAnnotationPositive.kt"),
            )
        RequestHandlingRules.httpMethodAnnotationRule.verify(positiveScope)
    }

    @Test
    fun `noTrailingSlashRule detects trailing slashes in paths`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/requestmapping/NoTrailingSlashNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                RequestHandlingRules.noTrailingSlashRule.verify(negativeScope)
            }
        assertEquals(
            "Trailing slash found in TrailingSlashController.trailingSlash: paths should not end with '/'",
            error.message,
        )
    }

    @Test
    fun `noTrailingSlashRule passes for paths without trailing slashes`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/requestmapping/NoTrailingSlashPositive.kt"),
            )
        RequestHandlingRules.noTrailingSlashRule.verify(positiveScope)
    }

    // ========== ResponseHandlingRules Tests ==========

    @Test
    fun `dtoSeparationRule detects Entity used as return type in Controller`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationNegative.kt",
                    "src/test/kotlin/fixtures/violations/web/responsehandling/domain/User.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ResponseHandlingRules.dtoSeparationRule.verify(negativeScope)
            }
        assertEquals(
            "Controller method EntityInControllerController.getUser returns JPA entity User. Use a DTO instead.",
            error.message,
        )
    }

    @Test
    fun `dtoSeparationRule passes for DTO used in Controller`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationPositive.kt"),
            )
        ResponseHandlingRules.dtoSeparationRule.verify(positiveScope)
    }

    @Test
    fun `dtoSeparationRule detects Entity wrapped in Set return type`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationSetNegative.kt",
                    "src/test/kotlin/fixtures/violations/web/responsehandling/domain/User.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ResponseHandlingRules.dtoSeparationRule.verify(negativeScope)
            }
        assertEquals(
            "Controller method SetEntityController.getUsers returns JPA entity User. Use a DTO instead.",
            error.message,
        )
    }

    @Test
    fun `dtoSeparationRule detects Entity wrapped in Page return type`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationPageNegative.kt",
                    "src/test/kotlin/fixtures/violations/web/responsehandling/PageStub.kt",
                    "src/test/kotlin/fixtures/violations/web/responsehandling/domain/User.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ResponseHandlingRules.dtoSeparationRule.verify(negativeScope)
            }
        assertEquals(
            "Controller method PageEntityController.getUsers returns JPA entity User. Use a DTO instead.",
            error.message,
        )
    }

    @Test
    fun `dtoSeparationRule detects Entity in non-first generic argument`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationMapNegative.kt",
                    "src/test/kotlin/fixtures/violations/web/responsehandling/domain/User.kt",
                ),
            )
        val error =
            assertFailsWith<AssertionError> {
                ResponseHandlingRules.dtoSeparationRule.verify(negativeScope)
            }
        assertEquals(
            "Controller method MapEntityController.getUserMap returns JPA entity User. Use a DTO instead.",
            error.message,
        )
    }

    // ========== TransactionRules Tests ==========

    @Test
    fun `transactionalPlacementRule detects Transactional on Controller`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/transaction/TransactionalPlacementNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                JpaRules.transactionalPlacementRule.verify(negativeScope)
            }
        assertEquals(
            "Controller TransactionalController has @Transactional annotation. Transactions should be managed at the service layer.",
            error.message,
        )
    }

    @Test
    fun `transactionalPlacementRule passes for Transactional on Service`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/transaction/TransactionalPlacementPositive.kt"),
            )
        JpaRules.transactionalPlacementRule.verify(positiveScope)
    }
}
