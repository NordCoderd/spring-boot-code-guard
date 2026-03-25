package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.general.CoreRules
import dev.protsenko.codeguard.rules.naming.NamingRules
import dev.protsenko.codeguard.rules.general.noFieldInjectionRule
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class SuppressionTest {

    // ========== notSuppressedClasses ==========

    @Test
    fun `statelessConfigurationRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/StatelessConfigurationSuppressedFixture.kt"),
        )
        CoreRules.statelessConfigurationRule.verify(scope)
    }

    @Test
    fun `statelessConfigurationRule still reports non-suppressed violations in mixed scope`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/StatelessConfigurationMixedFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            CoreRules.statelessConfigurationRule.verify(scope)
        }
        assert("ViolatingConfig" in error.message!!) {
            "Expected ViolatingConfig in error but got: ${error.message}"
        }
        assert("SuppressedMixedConfig" !in error.message!!) {
            "Expected SuppressedMixedConfig to be suppressed but it appeared in: ${error.message}"
        }
    }

    // ========== notSuppressedProperties ==========

    @Test
    fun `noFieldInjectionRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/NoFieldInjectionSuppressedFixture.kt"),
        )
        noFieldInjectionRule.verify(scope)
    }

    @Test
    fun `noFieldInjectionRule still reports non-suppressed violations in mixed scope`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/NoFieldInjectionMixedFixture.kt"),
        )
        val error = assertFailsWith<AssertionError> {
            noFieldInjectionRule.verify(scope)
        }
        assert("ViolatingService" in error.message!!) {
            "Expected ViolatingService in error but got: ${error.message}"
        }
        assert("SuppressedMixedInjectionService" !in error.message!!) {
            "Expected SuppressedMixedInjectionService to be suppressed but it appeared in: ${error.message}"
        }
    }

    // ========== notSuppressedFunctions ==========

    @Test
    fun `beanMethodsInConfigurationRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/BeanMethodsSuppressedFixture.kt"),
        )
        CoreRules.beanMethodsInConfigurationRule.verify(scope)
    }

    // ========== notSuppressedClassesAndInterfaces ==========

    @Test
    fun `repositoryNamingRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/RepositoryNamingSuppressedFixture.kt"),
        )
        NamingRules.repositoryNamingRule.verify(scope)
    }

    @Test
    fun `noStackTracePrintRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/suppression/NoStackTracePrintSuppressedFixture.kt"),
        )
        CoreRules.noStackTracePrintRule.verify(scope)
    }

    @Test
    fun `configurationPropertiesPrefixKebabCaseRule passes when class is suppressed`() {
        val scope = Konsist.scopeFromFiles(
            listOf(
                "src/test/kotlin/fixtures/violations/suppression/ConfigurationPropertiesPrefixKebabCaseSuppressedFixture.kt",
            ),
        )
        dev.protsenko.codeguard.rules.packages.PackageRules
            .configurationPropertiesPrefixKebabCaseRule
            .verify(scope)
    }
}
