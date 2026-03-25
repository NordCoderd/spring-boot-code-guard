package dev.protsenko.codeguard.core

import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration

/**
 * Shared repository detection for rules that target Spring Data repositories.
 */
internal fun KoInterfaceDeclaration.isSpringRepositoryInterface(): Boolean {
    val hasRepositoryAnnotation = this.hasAnnotationWithName("org.springframework.stereotype.Repository", "Repository")
    val extendsSpringDataRepo =
        this.text.let { text ->
            text.contains("JpaRepository") ||
                text.contains("CrudRepository") ||
                text.contains("PagingAndSortingRepository") ||
                text.contains("JpaSpecificationExecutor")
        }

    return hasRepositoryAnnotation || extendsSpringDataRepo
}
