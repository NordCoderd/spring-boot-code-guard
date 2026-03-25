package dev.protsenko.codeguard.rules.general

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.core.notSuppressedFunctions
import dev.protsenko.codeguard.core.notSuppressedProperties
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules related to Spring @Configuration classes and bean definitions.
 */
object CoreRules {
    private val consolePrintRegex = Regex("""\b(?:println|print)\s*\(""")
    private val stackTracePrintRegex = Regex("""\.\s*printStackTrace\s*\(""")
    private val stringLiteralRegex = Regex(""""(?:\\.|[^"\\])*"""")

    private fun String.withoutStringLiterals(): String =
        replace(stringLiteralRegex, "\"\"")

    /**
     * Rule: @Configuration classes should not have mutable state.
     * Configuration classes should be stateless and only define beans.
     */
    val statelessConfigurationRule =
        object : SpringBootRule {
            override val description = "@Configuration classes should be stateless (no var properties)"
            override val suppressKey = "CodeGuard:statelessConfiguration"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.CONFIGURATION)
                    .filterNot { it.hasAnnotationWithName(SpringAnnotations.CONFIGURATION_PROPERTIES) }
                    .forEach { klass ->
                        val mutableProperties =
                            klass
                                .properties()
                                .filter { it.isVar }
                                .filterNot {
                                    it.hasAnnotationWithName(SpringAnnotations.VALUE)
                                }

                        if (mutableProperties.isNotEmpty()) {
                            val propNames = mutableProperties.joinToString(", ") { it.name }
                            throw AssertionError(
                                "${klass.name} has mutable properties: $propNames. " +
                                    "@Configuration classes should be stateless.",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: @Bean methods should be defined in @Configuration classes.
     */
    val beanMethodsInConfigurationRule =
        object : SpringBootRule {
            override val description = "@Bean methods should be in @Configuration classes"
            override val suppressKey = "CodeGuard:beanMethodsInConfiguration"

            override fun verify(scope: KoScope) {
                val configClasses =
                    scope
                        .notSuppressedClasses(suppressKey)
                        .withAnnotationNamed(SpringAnnotations.CONFIGURATION)
                        .map { it.name }
                        .toSet()

                scope
                    .notSuppressedFunctions(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.BEAN)
                    .forEach { function ->
                        val containingClassName = function.containingDeclaration.toString()
                        val isInConfigClass = configClasses.contains(containingClassName)

                        if (!isInConfigClass) {
                            val message =
                                "@Bean method ${function.name} in $containingClassName " +
                                    "should be in a @Configuration class"
                            throw AssertionError(message)
                        }
                    }
            }
        }

    /**
     * Rule: Custom exceptions should extend RuntimeException or proper Spring exceptions.
     */
    val customExceptionStructureRule =
        object : SpringBootRule {
            override val description = "Custom exceptions should extend RuntimeException or proper Spring exceptions"
            override val suppressKey = "CodeGuard:customExceptionStructure"

            override fun verify(scope: KoScope) {
                val validExceptionBases =
                    setOf(
                        "RuntimeException",
                        "IllegalArgumentException",
                        "IllegalStateException",
                        "ResponseStatusException",
                        "NestedRuntimeException",
                    )

                scope
                    .notSuppressedClasses(suppressKey)
                    .filter { it.name.endsWith("Exception") }
                    .filterNot { klass ->
                        val parentNameFromModel = klass.parentClass?.name
                        val parentNameFromText =
                            Regex(""":\s*([A-Za-z0-9_.]+)""")
                                .find(klass.text)
                                ?.groupValues
                                ?.getOrNull(1)
                                ?.substringAfterLast('.')
                        val parentName = parentNameFromModel ?: parentNameFromText ?: "Any"

                        validExceptionBases.contains(parentName) ||
                            (parentName.endsWith("Exception") && parentName != "Exception")
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses =
                                violations.joinToString(", ") {
                                    val parentNameFromModel = it.parentClass?.name
                                    val parentNameFromText =
                                        Regex(""":\s*([A-Za-z0-9_.]+)""")
                                            .find(it.text)
                                            ?.groupValues
                                            ?.getOrNull(1)
                                            ?.substringAfterLast('.')
                                    "${it.name} extends ${parentNameFromModel ?: parentNameFromText ?: "Any"}"
                                }
                            throw AssertionError(
                                "Custom exception classes should extend RuntimeException or proper Spring exceptions: $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: No field-based dependency injection with @Autowired or @Inject.
     * Field injection makes testing harder and hides dependencies.
     */
    val noFieldInjectionRule =
        object : SpringBootRule {
            override val description = "No @Autowired or @Inject on fields"
            override val suppressKey = "CodeGuard:noFieldInjection"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedProperties(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.injectionAnnotations)
                    .also { properties ->
                        if (properties.isNotEmpty()) {
                            val violatingClasses =
                                properties
                                    .map { it.containingDeclaration.toString() }
                                    .distinct()
                                    .joinToString(", ")

                            throw AssertionError(
                                "Found ${properties.size} field(s) with @Autowired/@Inject in: $violatingClasses. " +
                                    "Use constructor injection instead.",
                            )
                        }
                    }
            }
        }

    val noProxyAnnotationsOnPrivateMethodsRule =
        object : SpringBootRule {
            override val description = "Spring proxy annotations must not be placed on private methods"
            override val suppressKey = "CodeGuard:noProxyAnnotationsOnPrivateMethods"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedFunctions(suppressKey)
                    .filter { it.hasModifier(KoModifier.PRIVATE) }
                    .forEach { function ->
                        val annotation = SpringAnnotations.proxyAnnotations
                            .firstOrNull { function.hasAnnotationWithName(it) }
                            ?: return@forEach
                        val annotationName = annotation.substringAfterLast(".")
                        throw AssertionError(
                            "${function.containingDeclaration}.${function.name} is private and " +
                                "annotated with @$annotationName — Spring proxy cannot intercept private methods, " +
                                "the annotation will be silently ignored.",
                        )
                    }
            }
        }

    val loggerInsteadOfPrintRule =
        object : SpringBootRule {
            override val description = "Logger should be used instead of println"
            override val suppressKey = "CodeGuard:loggerInsteadOfPrint"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.springBeanAnnotations)
                    .forEach { klass ->
                        klass
                            .functions()
                            .filter { function ->
                                consolePrintRegex.containsMatchIn(function.text)
                            }.also { violations ->
                                if (violations.isNotEmpty()) {
                                    val methodNames = violations.joinToString(", ") { it.name }
                                    throw AssertionError(
                                        "${klass.name} uses println/print in method(s): $methodNames. " +
                                            "Use proper logging (SLF4J) instead of console output.",
                                    )
                                }
                            }
                    }
            }
        }

    val noStackTracePrintRule =
        object : SpringBootRule {
            override val description = "Logger should be used instead of printStackTrace"
            override val suppressKey = "CodeGuard:noStackTracePrint"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.springBeanAnnotations)
                    .forEach { klass ->
                        klass
                            .functions()
                            .filter { function ->
                                stackTracePrintRegex.containsMatchIn(function.text.withoutStringLiterals())
                            }.also { violations ->
                                if (violations.isNotEmpty()) {
                                    val methodNames = violations.joinToString(", ") { it.name }
                                    throw AssertionError(
                                        "${klass.name} prints stack traces in method(s): $methodNames. " +
                                            "Use proper logging instead of printStackTrace().",
                                    )
                                }
                            }
                    }
            }
        }
}

val noFieldInjectionRule = CoreRules.noFieldInjectionRule
val customExceptionStructureRule = CoreRules.customExceptionStructureRule

val allCoreRules: List<SpringBootRule> =
    listOf(
        CoreRules.noFieldInjectionRule,
        CoreRules.statelessConfigurationRule,
        CoreRules.beanMethodsInConfigurationRule,
        CoreRules.customExceptionStructureRule,
        CoreRules.loggerInsteadOfPrintRule,
        CoreRules.noStackTracePrintRule,
        CoreRules.noProxyAnnotationsOnPrivateMethodsRule,
    )
