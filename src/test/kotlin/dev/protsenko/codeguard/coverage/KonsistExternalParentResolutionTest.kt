package dev.protsenko.codeguard.coverage

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.provider.KoFullyQualifiedNameProvider
import org.junit.jupiter.api.Test
import org.springframework.data.repository.Repository
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Validates how Konsist resolves transitive external parents across jar boundaries.
 *
 * Fixture: interface JpaRepositoryWithoutAnnotation : JpaRepository<Any, Long>
 * Chain:   JpaRepositoryWithoutAnnotation → JpaRepository → ... → Repository (all in Spring Data jars)
 */
class KonsistExternalParentResolutionTest {
    private val scope =
        Konsist.scopeFromFiles(
            listOf("src/test/kotlin/fixtures/violations/packages/repository/JpaRepositoryPositive.kt"),
        )

    private val iface by lazy {
        scope.interfaces().single { it.name == "JpaRepositoryWithoutAnnotation" }
    }

    @Test
    fun `hasExternalParentOf(Repository class, indirectParents=true) cannot traverse through jar boundaries`() {
        // JpaRepository → Repository chain lives entirely in Spring Data jars.
        // Konsist stops traversal at the first external (jar-based) declaration,
        // so Repository is never reached.
        assertFalse(
            iface.hasExternalParentOf(Repository::class, indirectParents = true),
            "Expected Konsist NOT to resolve Repository through jar boundary — but it did",
        )
    }

    @Test
    fun `print all parent categories for diagnosis`() {
        println("=== parents() ===")
        iface.parents().forEach { p ->
            println("  name=${p.name}  isExternal=${p.sourceDeclaration?.isExternal}  isInterface=${p.sourceDeclaration?.isInterface}  fqn=${(p.sourceDeclaration as? KoFullyQualifiedNameProvider)?.fullyQualifiedName}")
        }
        println("=== parentInterfaces() ===")
        iface.parentInterfaces().forEach { println("  ${it.name}") }
        println("=== externalParents() ===")
        iface.externalParents().forEach { println("  ${it.name}") }
        assertTrue(true)
    }

    @Test
    fun `JpaRepository is visible as a direct external parent but name includes type parameters`() {
        // parent.name returns "JpaRepository<Any, Long>" — type params are NOT stripped at the parent level
        assertFalse(
            iface.hasExternalParentWithName("JpaRepository"),
            "hasExternalParentWithName does an exact match — type params break it",
        )
        // sourceDeclaration.name DOES strip type params, so FQN lookup is the reliable path
        assertTrue(
            iface.externalParents().any { it.name.startsWith("JpaRepository") },
            "Expected at least one external parent whose name starts with JpaRepository",
        )
    }

    @Test
    fun `parentInterfaces() is empty for jar-based interfaces because isInterface=false on KoExternalDeclaration`() {
        // Konsist marks external declarations as isInterface=false regardless of their actual kind
        assertTrue(
            iface.parentInterfaces().isEmpty(),
            "Expected parentInterfaces() to be empty for a jar-based parent",
        )
    }

    @Test
    fun `FQN prefix org-springframework-data-repository misses JpaRepository which lives in jpa sub-package`() {
        // JpaRepository FQN: org.springframework.data.jpa.repository.JpaRepository
        // That does NOT start with org.springframework.data.repository
        assertFalse(
            iface.hasExternalParent { parent ->
                (parent.sourceDeclaration as? KoFullyQualifiedNameProvider)
                    ?.fullyQualifiedName
                    ?.startsWith("org.springframework.data.repository") == true
            },
            "Expected org.springframework.data.repository prefix to miss JpaRepository",
        )
    }

    @Test
    fun `FQN contains repository segment detects Spring Data repositories generically`() {
        // All Spring Data repository interfaces have "repository" in their package segment.
        // This is narrower than org.springframework.data.* but still covers all variants.
        assertTrue(
            iface.hasExternalParent { parent ->
                val fqn = (parent.sourceDeclaration as? KoFullyQualifiedNameProvider)?.fullyQualifiedName
                fqn != null && fqn.startsWith("org.springframework.data") && fqn.contains(".repository.")
            },
            "Expected FQN to contain .repository. segment",
        )
    }
}
