package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.isSpringRepositoryInterface
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules for controller dependencies and usage constraints.
 */
object ControllerUsingRules {
    /**
     * Rule: Controllers should not directly access repositories.
     * Controllers should only depend on services.
     */
    val controllerRepositoryRule =
        object : SpringBootRule {
            override val description = "Controllers should not directly access repositories"
            override val suppressKey = "CodeGuard:controllerRepository"

            override fun verify(scope: KoScope) {
                val repositoryClasses =
                    (
                        scope
                            .notSuppressedClasses(suppressKey)
                            .withAnnotationNamed(SpringAnnotations.REPOSITORY)
                            .map { it.name } +
                            scope
                                .interfaces()
                                .filter { it.isSpringRepositoryInterface() }
                                .map { it.name }
                    ).toSet()

                val failures = scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.controllerAnnotations)
                    .flatMap { controller ->
                        val constructorParamNames = controller.primaryConstructor
                            ?.parameters?.map { it.name }?.toSet() ?: emptySet()
                        val constructorViolation = controller.primaryConstructor?.parameters
                            ?.firstOrNull { repositoryClasses.contains(it.type.name) }
                            ?.let { param ->
                                "Controller ${controller.name} directly depends on repository " +
                                    "${param.type.name}. Controllers should only depend on services."
                            }
                        val propertyViolation = controller.properties()
                            .filter { it.name !in constructorParamNames }
                            .firstOrNull { repositoryClasses.contains(it.type?.name) }
                            ?.let { property ->
                                "Controller ${controller.name} has repository ${property.type?.name} " +
                                    "as property. Controllers should only depend on services."
                            }
                        listOfNotNull(constructorViolation, propertyViolation)
                    }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
            }
        }
}
