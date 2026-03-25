package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
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

    private val genericWrappers =
        setOf(
            "ResponseEntity",
            "List",
            "MutableList",
            "Set",
            "MutableSet",
            "Collection",
            "Iterable",
            "Page",
        )

    private fun normalizeType(typeName: String): String =
        typeName
            .trim()
            .removeSuffix("?")
            .removePrefix("out ")
            .removePrefix("in ")

    private fun splitTopLevelTypeArguments(arguments: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var depth = 0

        arguments.forEach { char ->
            when (char) {
                '<' -> {
                    depth++
                    current.append(char)
                }

                '>' -> {
                    depth--
                    current.append(char)
                }

                ',' if depth == 0 -> {
                    result.add(current.toString().trim())
                    current.clear()
                }

                else -> {
                    current.append(char)
                }
            }
        }

        val tail = current.toString().trim()
        if (tail.isNotEmpty()) {
            result.add(tail)
        }

        return result
    }

    private fun extractTypeCandidates(typeName: String): LinkedHashSet<String> {
        val normalized = normalizeType(typeName)
        if (normalized.isEmpty() || normalized == "*") {
            return linkedSetOf()
        }

        val genericStart = normalized.indexOf('<')
        if (genericStart < 0 || !normalized.endsWith(">")) {
            return linkedSetOf(normalized.substringAfterLast("."))
        }

        val rawType = normalized.substringBefore("<").substringAfterLast(".")
        val inner = normalized.substring(genericStart + 1, normalized.length - 1)
        val result = linkedSetOf<String>()

        if (rawType !in genericWrappers) {
            result.add(rawType)
        }

        splitTopLevelTypeArguments(inner).forEach { argument ->
            result.addAll(extractTypeCandidates(argument))
        }

        return result
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

                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.REST_CONTROLLER)
                    .flatMap { it.functions() }
                    .forEach { function ->
                        // Check return type
                        function.returnType?.let { returnType ->
                            val returnTypeName =
                                extractTypeCandidates(returnType.name)
                                    .firstOrNull { entityClasses.contains(it) }

                            if (returnTypeName != null) {
                                throw AssertionError(
                                    "Controller method ${function.containingDeclaration}.${function.name} " +
                                        "returns JPA entity $returnTypeName. Use a DTO instead.",
                                )
                            }
                        }

                        // Check parameters
                        function.parameters.forEach { param ->
                            val paramTypeName =
                                extractTypeCandidates(param.type.name)
                                    .firstOrNull { entityClasses.contains(it) }

                            if (paramTypeName != null) {
                                throw AssertionError(
                                    "Controller method ${function.containingDeclaration}.${function.name} " +
                                        "accepts JPA entity $paramTypeName as parameter. Use a DTO instead.",
                                )
                            }
                        }
                    }
            }
        }
}
