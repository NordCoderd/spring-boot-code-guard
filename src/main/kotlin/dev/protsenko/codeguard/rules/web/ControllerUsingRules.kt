package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.isSpringRepositoryInterface
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

            override fun verify(scope: KoScope) {
                val repositoryClasses =
                    (
                        scope
                            .classes()
                            .withAnnotationNamed(SpringAnnotations.REPOSITORY)
                            .map { it.name } +
                            scope
                                .interfaces()
                                .filter { it.isSpringRepositoryInterface() }
                                .map { it.name }
                    ).toSet()

                scope
                    .classes()
                    .withAnnotationNamed(SpringAnnotations.controllerAnnotations).forEach { controller ->
                        // Check constructor parameters
                        controller.primaryConstructor?.parameters?.forEach { param ->
                            if (repositoryClasses.contains(param.type.name)) {
                                val message =
                                    "Controller ${controller.name} directly depends on repository " +
                                        "${param.type.name}. Controllers should only depend on services."
                                throw AssertionError(message)
                            }
                        }

                        // Check properties
                        controller.properties().forEach { property ->
                            if (repositoryClasses.contains(property.type?.name)) {
                                val message =
                                    "Controller ${controller.name} has repository ${property.type?.name} " +
                                        "as property. Controllers should only depend on services."
                                throw AssertionError(message)
                            }
                        }
                    }
            }
        }
}
