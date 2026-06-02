package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.extractTypeCandidates
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules for service-layer dependencies and usage constraints.
 */
object ServiceUsingRules {
    private val webImportPrefixes =
        listOf(
            "org.springframework.web.",
            "org.springframework.http.",
            "jakarta.servlet.",
            "javax.servlet.",
        )

    /**
     * Rule: Services should not depend on the web layer.
     * Detected by import statements pointing at Spring Web, Spring HTTP, the
     * Servlet API, or in-project controllers.
     */
    val serviceWebIndependenceRule =
        object : SpringBootRule {
            override val description = "Services should not depend on the web layer"
            override val suppressKey = "CodeGuard:serviceWebDependency"

            override fun verify(scope: KoScope) {
                val controllerFqns =
                    (
                        scope.notSuppressedClasses(suppressKey)
                            .withAnnotationNamed(SpringAnnotations.controllerAnnotations) +
                            scope.interfaces().withAnnotationNamed(SpringAnnotations.controllerAnnotations)
                    ).mapNotNull { it.fullyQualifiedName }.toSet()

                val failures =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .withAnnotationNamed(SpringAnnotations.SERVICE)
                        .mapNotNull { service ->
                            val forbidden =
                                service.containingFile.imports
                                    .map { it.name }
                                    .filter { name ->
                                        webImportPrefixes.any { name.startsWith(it) } || name in controllerFqns
                                    }
                            if (forbidden.isNotEmpty()) {
                                "Service ${service.name} depends on web-layer classes: " +
                                    "${forbidden.joinToString(", ")}. " +
                                    "Service layer should not depend on the web layer."
                            } else {
                                null
                            }
                        }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
            }
        }

    /**
     * Rule: Services should not return JPA entities.
     * Public service methods must return DTOs, not the persistence model.
     */
    val serviceEntityReturnRule =
        object : SpringBootRule {
            override val description = "Services should not return JPA entities"
            override val suppressKey = "CodeGuard:serviceEntityReturn"

            override fun verify(scope: KoScope) {
                val entityClasses =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .withAnnotationNamed(SpringAnnotations.entityAnnotations)
                        .map { it.name }
                        .toSet()

                val failures =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .withAnnotationNamed(SpringAnnotations.SERVICE)
                        .flatMap { it.functions() }
                        .filter { it.hasPublicOrDefaultModifier }
                        .mapNotNull { function ->
                            function.returnType?.let { returnType ->
                                extractTypeCandidates(returnType.name)
                                    .firstOrNull { entityClasses.contains(it) }
                                    ?.let { entityName ->
                                        "Service method ${function.containingDeclaration}.${function.name} " +
                                            "returns JPA entity $entityName. Use a DTO instead."
                                    }
                            }
                        }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
            }
        }
}
