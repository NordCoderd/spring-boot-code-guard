package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.packages.PackageRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for package domain rules.
 */
class PackageRulesViolationTest {
    @Test
    fun `packageNamingRule detects uppercase in package names`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/package/PackageNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.packageNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Package names should be lowercase: fixtures.violations.architecture.BadPackage",
            error.message,
        )
    }

    @Test
    fun `packageNamingRule passes for lowercase package names`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/architecture/package/PackageNamingPositive.kt"),
            )
        PackageRules.packageNamingRule.verify(positiveScope)
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
}
