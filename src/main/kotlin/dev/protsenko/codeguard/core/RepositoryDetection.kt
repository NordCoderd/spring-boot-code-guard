package dev.protsenko.codeguard.core

import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration

private val springDataRepositoryTypes =
    listOf("JpaRepository", "CrudRepository", "PagingAndSortingRepository", "JpaSpecificationExecutor")

/**
 * Shared repository detection for rules that target Spring Data repositories.
 */
internal fun KoInterfaceDeclaration.isSpringRepositoryInterface(): Boolean {
    val hasRepositoryAnnotation = this.hasAnnotationWithName("org.springframework.stereotype.Repository", "Repository")
    // Match declared supertypes only, so mentions in comments or member names do not count.
    // endsWith keeps variants such as ListCrudRepository or ReactiveCrudRepository.
    val extendsSpringDataRepo =
        parents().any { parent ->
            val simpleName = parent.name.substringBefore('<').substringAfterLast('.')
            springDataRepositoryTypes.any { simpleName.endsWith(it) }
        }

    return hasRepositoryAnnotation || extendsSpringDataRepo
}
