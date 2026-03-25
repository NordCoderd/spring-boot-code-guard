package dev.protsenko.codeguard.rules.web

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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
     * Rule: @RequestBody/@RequestParam should have @Valid or @Validated annotation.
     * This ensures proper validation of incoming request data.
     */
    val requestValidationRule =
        object : SpringBootRule {
            override val description = "@RequestBody/@RequestParam should have @Valid or @Validated"

            override fun verify(scope: KoScope) {
                scope
                    .classes()
                    .withAnnotationNamed(SpringAnnotations.REST_CONTROLLER)
                    .flatMap { it.functions() }
                    .filter { function ->
                        SpringAnnotations.httpMappingAnnotations
                            .any { annotation -> function.hasAnnotationWithName(annotation) }
                    }.forEach { function ->
                        val requestBodyViolations =
                            function.parameters
                                .filter {
                                    it.hasAnnotationWithName(SpringAnnotations.REQUEST_BODY) ||
                                        it.text.contains("@${SpringAnnotations.REQUEST_BODY}")
                                }.filterNot { param ->
                                    param.hasAnnotationWithName(SpringAnnotations.validationAnnotations)
                                }

                        if (requestBodyViolations.isNotEmpty()) {
                            val paramNames = requestBodyViolations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Controller method ${function.containingDeclaration}.${function.name} " +
                                    "has @RequestBody parameter(s) without @Valid or @Validated: $paramNames. " +
                                    "Validation should be enforced for request bodies.",
                            )
                        }

                        val requestParamViolations =
                            function.parameters
                                .filter {
                                    it.hasAnnotationWithName(SpringAnnotations.REQUEST_PARAM) ||
                                        it.text.contains("@${SpringAnnotations.REQUEST_PARAM}")
                                }.filterNot { param ->
                                    param.hasAnnotationWithName(SpringAnnotations.validationAnnotations)
                                }

                        if (requestParamViolations.isNotEmpty()) {
                            val paramNames = requestParamViolations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Controller method ${function.containingDeclaration}.${function.name} " +
                                    "has @RequestParam parameter(s) without @Valid or @Validated: $paramNames. " +
                                    "Validation should be enforced for request params.",
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

            private fun hasTrailingSlash(pathValue: String): Boolean =
                pathValue.contains("\"/") && pathValue.matches(Regex(".*\"[^\"]+/\".*"))

            override fun verify(scope: KoScope) {
                scope
                    .functions()
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
