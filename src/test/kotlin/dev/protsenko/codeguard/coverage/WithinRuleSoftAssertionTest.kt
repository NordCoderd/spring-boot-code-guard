package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.general.CoreRules
import dev.protsenko.codeguard.rules.jpa.JpaRules
import dev.protsenko.codeguard.rules.proxy.ProxyRules
import dev.protsenko.codeguard.rules.web.ControllerUsingRules
import dev.protsenko.codeguard.rules.web.RequestHandlingRules
import dev.protsenko.codeguard.rules.web.ResponseHandlingRules
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WithinRuleSoftAssertionTest {

    @Test
    fun `noSelfInvocationOfProxyMethodsRule reports violations from all classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/SelfInvocationOfProxyMultipleClassViolationFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            ProxyRules.noSelfInvocationOfProxyMethodsRule.verify(scope)
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

    @Test
    fun `statelessConfigurationRule reports violations from all Configuration classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/configuration/StatelessConfigurationMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.statelessConfigurationRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("StatefulConfigAlpha"),
            "Expected StatefulConfigAlpha violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("StatefulConfigBeta"),
            "Expected StatefulConfigBeta violation in error: ${error.message}",
        )
    }

    @Test
    fun `beanMethodsInConfigurationRule reports violations from all Bean methods not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsInConfigurationMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.beanMethodsInConfigurationRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("beanAlpha"),
            "Expected beanAlpha violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("beanBeta"),
            "Expected beanBeta violation in error: ${error.message}",
        )
    }

    @Test
    fun `noProxyAnnotationsOnPrivateMethodsRule reports violations from all private methods not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/proxy/ProxyOnPrivateMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.noProxyAnnotationsOnPrivateMethodsRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("PrivateProxyAlphaService"),
            "Expected PrivateProxyAlphaService violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("PrivateProxyBetaService"),
            "Expected PrivateProxyBetaService violation in error: ${error.message}",
        )
    }

    @Test
    fun `loggerInsteadOfPrintRule reports violations from all classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/observability/LoggerInsteadOfPrintMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.loggerInsteadOfPrintRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("PrintlnAlphaService"),
            "Expected PrintlnAlphaService violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("PrintlnBetaService"),
            "Expected PrintlnBetaService violation in error: ${error.message}",
        )
    }

    @Test
    fun `noStackTracePrintRule reports violations from all classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/observability/NoStackTracePrintMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.noStackTracePrintRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("StackTraceAlphaService"),
            "Expected StackTraceAlphaService violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("StackTraceBetaService"),
            "Expected StackTraceBetaService violation in error: ${error.message}",
        )
    }

    @Test
    fun `entityIdRule reports violations from all Entity classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/jpa/entity/EntityIdMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            JpaRules.entityIdRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("MultiEntityWithoutIdAlpha"),
            "Expected MultiEntityWithoutIdAlpha violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("MultiEntityWithoutIdBeta"),
            "Expected MultiEntityWithoutIdBeta violation in error: ${error.message}",
        )
    }

    @Test
    fun `transactionalPlacementRule reports violations from all controllers not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/jpa/transaction/TransactionalPlacementMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            JpaRules.transactionalPlacementRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("TransactionalAlphaController"),
            "Expected TransactionalAlphaController violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("TransactionalBetaController"),
            "Expected TransactionalBetaController violation in error: ${error.message}",
        )
    }

    @Test
    fun `domainLayerIndependenceRule reports violations from all domain classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/architecture/layer/DomainLayerIndependenceMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            JpaRules.domainLayerIndependenceRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("MultiDomainAlpha"),
            "Expected MultiDomainAlpha violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("MultiDomainBeta"),
            "Expected MultiDomainBeta violation in error: ${error.message}",
        )
    }

    @Test
    fun `noTrailingSlashRule reports violations from all methods not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/web/requestmapping/NoTrailingSlashMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            RequestHandlingRules.noTrailingSlashRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("alpha"),
            "Expected alpha method violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("beta"),
            "Expected beta method violation in error: ${error.message}",
        )
    }

    @Test
    fun `controllerRepositoryRule reports violations from all controllers not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf(
                "src/test/kotlin/fixtures/violations/architecture/layer/ControllerRepositoryMultiNegative.kt",
                "src/test/kotlin/fixtures/violations/architecture/layer/repository/UserRepository.kt",
            ),
        )
        val error = assertFailsWith<AssertionError> {
            ControllerUsingRules.controllerRepositoryRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("MultiControllerAlpha"),
            "Expected MultiControllerAlpha violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("MultiControllerBeta"),
            "Expected MultiControllerBeta violation in error: ${error.message}",
        )
    }

    @Test
    fun `configurationPropertiesPrefixKebabCaseRule reports violations from all classes not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/core/config/ConfigurationPropertiesPrefixKebabCaseMultiNegative.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            dev.protsenko.codeguard.rules.packages.PackageRules.configurationPropertiesPrefixKebabCaseRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("MultiKebabAlphaProperties"),
            "Expected MultiKebabAlphaProperties violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("MultiKebabBetaProperties"),
            "Expected MultiKebabBetaProperties violation in error: ${error.message}",
        )
    }

    @Test
    fun `dtoSeparationRule reports violations from all controller methods not just first`() {
        val scope = Konsist.scopeFromFiles(
            listOf(
                "src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationMultiNegative.kt",
                "src/test/kotlin/fixtures/violations/web/responsehandling/domain/User.kt",
            ),
        )
        val error = assertFailsWith<AssertionError> {
            ResponseHandlingRules.dtoSeparationRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("getAlpha"),
            "Expected getAlpha violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("getBeta"),
            "Expected getBeta violation in error: ${error.message}",
        )
    }

    @Test
    fun `controllerRepositoryRule reports both constructor and property violations for same controller`() {
        val scope = Konsist.scopeFromFiles(
            listOf(
                "src/test/kotlin/fixtures/violations/architecture/layer/ControllerRepositoryBothViolationsNegative.kt",
                "src/test/kotlin/fixtures/violations/architecture/layer/repository/UserRepository.kt",
            ),
        )
        val error = assertFailsWith<AssertionError> {
            ControllerUsingRules.controllerRepositoryRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("directly depends on repository"),
            "Expected constructor violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("has repository"),
            "Expected property violation in error: ${error.message}",
        )
    }

    @Test
    fun `dtoSeparationRule reports both return type and parameter violations for same method`() {
        val scope = Konsist.scopeFromFiles(
            listOf(
                "src/test/kotlin/fixtures/violations/web/responsehandling/DtoSeparationBothViolationsNegative.kt",
                "src/test/kotlin/fixtures/violations/web/responsehandling/domain/User.kt",
            ),
        )
        val error = assertFailsWith<AssertionError> {
            ResponseHandlingRules.dtoSeparationRule.verify(scope)
        }
        assertTrue(
            error.message!!.contains("returns JPA entity"),
            "Expected return type violation in error: ${error.message}",
        )
        assertTrue(
            error.message!!.contains("accepts JPA entity"),
            "Expected parameter violation in error: ${error.message}",
        )
    }
}
