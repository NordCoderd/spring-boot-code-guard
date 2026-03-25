package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import dev.protsenko.codeguard.rules.jpa.JpaRules
import dev.protsenko.codeguard.rules.naming.NamingRules
import dev.protsenko.codeguard.rules.packages.PackageRules
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for JpaRules and NamingRules (repository)
 * with both positive and negative cases and error message assertions.
 */
class JpaRulesViolationTest {
    // ========== JpaRules Tests ==========

    @Test
    fun `entityPackageRule detects Entity not in domain or entity package`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/EntityPackageNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                PackageRules.entityPackageRule.verify(negativeScope)
            }
        assertEquals(
            "@Entity classes should be in .domain or .entity package: EntityNotInDomainPackage (fixtures.violations.jpa)",
            error.message,
        )
    }

    @Test
    fun `entityPackageRule passes for Entity in domain package`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/EntityPackagePositive.kt"),
            )
        PackageRules.entityPackageRule.verify(positiveScope)
    }

    @Test
    fun `entityIdRule detects Entity without Id field`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/EntityIdNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                JpaRules.entityIdRule.verify(negativeScope)
            }
        assertEquals(
            "@Entity class EntityWithoutId must have a field annotated with @Id",
            error.message,
        )
    }

    @Test
    fun `entityIdRule passes for Entity with Id field`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/EntityIdPositive.kt"),
            )
        JpaRules.entityIdRule.verify(positiveScope)
    }

    @Test
    fun `entityIdRule passes for Entity inheriting Id field from base class`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf(
                    "src/test/kotlin/fixtures/violations/jpa/entity/BaseEntityWithId.kt",
                    "src/test/kotlin/fixtures/violations/jpa/entity/EntityIdInheritedPositive.kt",
                ),
            )
        JpaRules.entityIdRule.verify(positiveScope)
    }

    @Test
    fun `noDataClassEntityRule detects data class used as Entity`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/DataClassEntityNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                JpaRules.noDataClassEntityRule.verify(negativeScope)
            }
        assertEquals(
            "Data classes should not be used as @Entity: DataClassEntity. " +
                "Data classes are incompatible with JPA due to their final nature, " +
                "structural equality, and immutability assumptions.",
            error.message,
        )
    }

    @Test
    fun `noDataClassEntityRule passes for regular open class Entity`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/RegularClassEntityPositive.kt"),
            )
        JpaRules.noDataClassEntityRule.verify(positiveScope)
    }

    @Test
    fun `noDataClassEntityRule passes for simple Entity without constructor`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/entity/SimpleEntityPositive.kt"),
            )
        JpaRules.noDataClassEntityRule.verify(positiveScope)
    }

    // ========== RepositoryRules Tests ==========

    @Test
    fun `repositoryNamingRule detects Repository without Repository suffix`() {
        val negativeScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/repository/RepositoryNamingNegative.kt"),
            )
        val error =
            assertFailsWith<AssertionError> {
                NamingRules.repositoryNamingRule.verify(negativeScope)
            }
        assertEquals(
            "Repository interfaces should end with 'Repository': BadRepoName",
            error.message,
        )
    }

    @Test
    fun `repositoryNamingRule passes for Repository with Repository suffix`() {
        val positiveScope =
            Konsist.scopeFromFiles(
                listOf("src/test/kotlin/fixtures/violations/jpa/repository/RepositoryNamingPositive.kt"),
            )
        NamingRules.repositoryNamingRule.verify(positiveScope)
    }
}
