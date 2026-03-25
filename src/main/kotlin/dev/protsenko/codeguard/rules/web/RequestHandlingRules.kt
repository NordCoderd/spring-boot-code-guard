package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.core.notSuppressedFunctions
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules for HTTP request mapping and REST endpoint conventions.
 */
object RequestHandlingRules {
    /**
     * Rule: Use specific HTTP method annotations instead of generic @RequestMapping.
     */
    val httpMethodAnnotationRule =
        object : SpringBootRule {
            override val description = "Use @GetMapping, @PostMapping etc. instead of @RequestMapping"
            override val suppressKey = "CodeGuard:httpMethodAnnotation"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.REST_CONTROLLER)
                    .flatMap { it.functions() }
                    .withAnnotationNamed(SpringAnnotations.REQUEST_MAPPING)
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingMethods =
                                violations.joinToString(", ") {
                                    "${it.containingDeclaration}.${it.name}"
                                }
                            throw AssertionError(
                                "Use specific HTTP method annotations (@GetMapping, @PostMapping, etc.) " +
                                    "instead of @RequestMapping: $violatingMethods",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: REST endpoint paths should not have trailing slashes.
     */
    val noTrailingSlashRule =
        object : SpringBootRule {
            override val description = "Request mapping paths should not have trailing slashes"
            override val suppressKey = "CodeGuard:noTrailingSlash"

            private fun hasTrailingSlash(pathValue: String): Boolean =
                pathValue.contains("\"/") && pathValue.matches(Regex(".*\"[^\"]+/\".*"))

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedFunctions(suppressKey)
                    .filter { function ->
                        SpringAnnotations.httpMappingAnnotations.any { function.hasAnnotationWithName(it) }
                    }.forEach { function ->
                        val violations =
                            function.annotations
                                .filter { it.fullyQualifiedName in SpringAnnotations.httpMappingAnnotations }
                                .filter { hasTrailingSlash(it.text) }

                        if (violations.isNotEmpty()) {
                            throw AssertionError(
                                "Trailing slash found in ${function.containingDeclaration}" +
                                    ".${function.name}: paths should not end with '/'",
                            )
                        }
                    }
            }
        }
}
