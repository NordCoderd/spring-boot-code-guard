package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.general.CoreRules
import dev.protsenko.codeguard.rules.general.noFieldInjectionRule
import dev.protsenko.codeguard.rules.naming.NamingRules
import dev.protsenko.codeguard.rules.packages.PackageRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for DependencyInjectionRules, NamingRules, and ConfigurationRules
 * with both positive and negative cases and error message assertions.
 */
class DependencyInjectionAndCoreRulesViolationTest {
    // ========== DependencyInjectionRules Tests ==========

    @Test
    fun `noFieldInjectionRule detects Autowired fields`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                noFieldInjectionRule.verify(negativeScope)
            }
        assertEquals(
            "Found 1 field(s) with @Autowired/@Inject in: ServiceWithFieldInjection. Use constructor injection instead.",
            error.message,
        )
    }

    @Test
    fun `noFieldInjectionRule passes for constructor injection`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/dependencyinjection/NoFieldInjectionPositive.kt"),
            )
        noFieldInjectionRule.verify(positiveScope)
    }

    // ========== NamingRules Tests ==========

    @Test
    fun `serviceNamingRule detects Service without Service suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/ComponentNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.serviceNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Classes with @Service annotation should end with 'Service': BadServiceName",
            error.message,
        )
    }

    @Test
    fun `serviceNamingRule passes for properly named Service`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/ComponentNamingPositive.kt"),
            )
        NamingRules.serviceNamingRule.verify(positiveScope)
    }

    @Test
    fun `repositoryNamingRule detects Repository interface without Repository suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/RepositoryInterfaceNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.repositoryNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Repository interfaces should end with 'Repository': BadRepoInterface",
            error.message,
        )
    }

    @Test
    fun `repositoryNamingRule passes for Repository interface with Repository suffix`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/RepositoryInterfaceNamingPositive.kt"),
            )
        NamingRules.repositoryNamingRule.verify(positiveScope)
    }

    @Test
    fun `repositoryNamingRule detects Repository class without Repository suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/RepositoryClassNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.repositoryNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Repository interfaces should end with 'Repository': BadRepoClass",
            error.message,
        )
    }

    @Test
    fun `repositoryNamingRule passes for Repository class with Repository suffix`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/RepositoryClassNamingPositive.kt"),
            )
        NamingRules.repositoryNamingRule.verify(positiveScope)
    }

    @Test
    fun `componentNamingRule detects Component without proper suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/ComponentNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.componentNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Classes with @Component annotation should end with 'Component', 'Factory', 'Provider', 'Listener', or 'Handler': BadName",
            error.message,
        )
    }

    @Test
    fun `componentNamingRule passes for Component with proper suffixes`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/ComponentNamingPositive.kt"),
            )
        NamingRules.componentNamingRule.verify(positiveScope)
    }

    @Test
    fun `componentNamingRule passes for Component in component package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/naming/ComponentInPackagePositive.kt"),
            )
        NamingRules.componentNamingRule.verify(positiveScope)
    }

    @Test
    fun `servicePackageRule detects Service not in service package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/ServicePackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.servicePackageRule.verify(negativeScope)
            }
        assertEquals(
            "@Service classes should be in .service package: ServiceNotInServicePackage (fixtures.violations.core.springcore)",
            error.message,
        )
    }

    @Test
    fun `servicePackageRule passes for Service in service package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/springcore/ServicePackagePositive.kt"),
            )
        PackageRules.servicePackageRule.verify(positiveScope)
    }

    // ========== ConfigurationRules Tests ==========

    @Test
    fun `configurationPackageRule detects Configuration not in config package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/ConfigurationPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.configurationPackageRule.verify(negativeScope)
            }
        assertEquals(
            "@Configuration classes should be in .config or .configuration package: ConfigNotInConfigPackage (fixtures.violations.core.settings)",
            error.message,
        )
    }

    @Test
    fun `configurationPackageRule passes for Configuration in config package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/ConfigurationPackagePositive.kt"),
            )
        PackageRules.configurationPackageRule.verify(positiveScope)
    }

    @Test
    fun `configurationPackageRule passes for Configuration in configuration package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/ConfigurationPackageConfigurationPositive.kt"),
            )
        PackageRules.configurationPackageRule.verify(positiveScope)
    }

    @Test
    fun `statelessConfigurationRule detects mutable state in Configuration`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/StatelessConfigurationNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                CoreRules.statelessConfigurationRule.verify(negativeScope)
            }
        assertEquals(
            "StatefulConfiguration has mutable properties: counter. @Configuration classes should be stateless.",
            error.message,
        )
    }

    @Test
    fun `statelessConfigurationRule passes for stateless Configuration`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/StatelessConfigurationPositive.kt"),
            )
        CoreRules.statelessConfigurationRule.verify(positiveScope)
    }

    @Test
    fun `beanMethodsInConfigurationRule detects Bean outside Configuration`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsInConfigurationNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                CoreRules.beanMethodsInConfigurationRule.verify(negativeScope)
            }
        assertEquals(
            "@Bean method someBean in NotAConfigClass should be in a @Configuration class",
            error.message,
        )
    }

    @Test
    fun `beanMethodsInConfigurationRule passes for Bean in Configuration`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsInConfigurationPositive.kt"),
            )
        CoreRules.beanMethodsInConfigurationRule.verify(positiveScope)
    }

    @Test
    fun `beanMethodsInConfigurationRule passes for Bean in Config when similarly named helper exists`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsNameCollisionPositive.kt"),
            )
        CoreRules.beanMethodsInConfigurationRule.verify(positiveScope)
    }

    @Test
    fun `beanMethodsInConfigurationRule detects Bean in similarly named helper class`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/configuration/BeanMethodsNameCollisionNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                CoreRules.beanMethodsInConfigurationRule.verify(negativeScope)
            }
        assertEquals(
            "@Bean method helperBean in AppConfigHelper should be in a @Configuration class",
            error.message,
        )
    }

    @Test
    fun `propertiesValidationRule detects ConfigurationProperties not in properties package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/config/PropertiesValidationNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.propertiesValidationRule.verify(negativeScope)
            }
        assertEquals(
            "@ConfigurationProperties classes should be in .properties package: AppProperties (fixtures.violations.core.wrongpackage)",
            error.message,
        )
    }

    @Test
    fun `propertiesValidationRule passes for ConfigurationProperties in properties package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/config/PropertiesValidationPositive.kt"),
            )
        PackageRules.propertiesValidationRule.verify(positiveScope)
    }

    @Test
    fun `propertiesValidationRule passes for ConfigurationProperties in property package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/core/config/PropertiesValidationPropertyPositive.kt"),
            )
        PackageRules.propertiesValidationRule.verify(positiveScope)
    }
}
