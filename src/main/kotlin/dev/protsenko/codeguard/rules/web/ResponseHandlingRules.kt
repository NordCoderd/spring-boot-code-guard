package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.extractTypeCandidates
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules for REST response handling, DTOs, and request/response contracts.
 */
object ResponseHandlingRules {
    /**
     * Rule: @RestController GET methods should not return void.
     */
    val restControllerReturnTypeRule =
        object : SpringBootRule {
            override val description = "@RestController GET methods should not return void"
            override val suppressKey = "CodeGuard:restControllerReturnType"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.REST_CONTROLLER)
                    .flatMap { it.functions() }
                    .withAnnotationNamed(SpringAnnotations.GET_MAPPING)
                    .filter { it.hasReturnType { returnType -> returnType.name == "Unit" } }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingMethods =
                                violations.joinToString(", ") {
                                    "${it.containingDeclaration}.${it.name}"
                                }
                            throw AssertionError(
                                "GET methods in @RestController should return a value: $violatingMethods",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: DTOs should be separate from JPA entities.
     * Classes with @Entity should not be used as controller parameters/return types.
     */
    val dtoSeparationRule =
        object : SpringBootRule {
            override val description = "Separate DTOs from JPA entities in controllers"
            override val suppressKey = "CodeGuard:dtoSeparation"

            override fun verify(scope: KoScope) {
                val entityClasses =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .withAnnotationNamed(SpringAnnotations.entityAnnotations)
                        .map { it.name }
                        .toSet()

                val failures = scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.REST_CONTROLLER)
                    .flatMap { it.functions() }
                    .flatMap { function ->
                        val returnViolation = function.returnType?.let { returnType ->
                            extractTypeCandidates(returnType.name)
                                .firstOrNull { entityClasses.contains(it) }
                                ?.let { returnTypeName ->
                                    "Controller method ${function.containingDeclaration}.${function.name} " +
                                        "returns JPA entity $returnTypeName. Use a DTO instead."
                                }
                        }
                        val paramViolation = function.parameters.firstNotNullOfOrNull { param ->
                            extractTypeCandidates(param.type.name)
                                .firstOrNull { entityClasses.contains(it) }
                                ?.let { paramTypeName ->
                                    "Controller method ${function.containingDeclaration}.${function.name} " +
                                        "accepts JPA entity $paramTypeName as parameter. Use a DTO instead."
                                }
                        }
                        listOfNotNull(returnViolation, paramViolation)
                    }
                if (failures.isNotEmpty()) throw AssertionError(failures.joinToString("\n"))
            }
        }
}
