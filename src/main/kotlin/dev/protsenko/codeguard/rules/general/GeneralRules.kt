package dev.protsenko.codeguard.rules.general

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.rules.SpringAnnotations

/**
 * Rules related to Spring @Configuration classes and bean definitions.
 */
object CoreRules {
    private val consolePrintRegex = Regex("""\b(?:println|print)\s*\(""")

    /**
     * Rule: @Configuration classes should not have mutable state.
     * Configuration classes should be stateless and only define beans.
     */
    val statelessConfigurationRule =
        object : SpringBootRule {
            override val description = "@Configuration classes should be stateless (no var properties)"

            override fun verify(scope: KoScope) {
                scope
                    .classes()
                    .withAnnotationNamed(SpringAnnotations.CONFIGURATION)
                    .forEach { klass ->
                        val mutableProperties =
                            klass
                                .properties()
                                .filter { it.isVar }
                                .filterNot {
                                    it.hasAnnotationWithName(SpringAnnotations.VALUE)
                                }.filterNot {
                                    it.hasAnnotationWithName(SpringAnnotations.CONFIGURATION_PROPERTIES)
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

            override fun verify(scope: KoScope) {
                val configClasses =
                    scope
                        .classes()
                        .withAnnotationNamed(SpringAnnotations.CONFIGURATION)
                        .map { it.name }
                        .toSet()

                scope
                    .functions()
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
                    .classes()
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

            override fun verify(scope: KoScope) {
                scope
                    .properties()
                    .withAnnotationNamed(SpringAnnotations.injectionAnnotations).also { properties ->
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

    val loggerInsteadOfPrintRule =
        object : SpringBootRule {
            override val description = "Logger should be used instead of println"

            override fun verify(scope: KoScope) {
                scope
                    .classes()
                    .withAnnotationNamed(SpringAnnotations.springBeanAnnotations).forEach { klass ->
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
}

val noFieldInjectionRule = CoreRules.noFieldInjectionRule
val customExceptionStructureRule = CoreRules.customExceptionStructureRule

val allCoreRules: List<SpringBootRule> = listOf(
    CoreRules.noFieldInjectionRule,
    CoreRules.statelessConfigurationRule,
    CoreRules.beanMethodsInConfigurationRule,
    CoreRules.customExceptionStructureRule,
    CoreRules.loggerInsteadOfPrintRule,
)
