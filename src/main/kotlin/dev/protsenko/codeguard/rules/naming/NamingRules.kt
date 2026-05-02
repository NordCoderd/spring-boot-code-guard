package dev.protsenko.codeguard.rules.naming

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.notSuppressedClasses
import dev.protsenko.codeguard.core.notSuppressedClassesAndInterfaces
import dev.protsenko.codeguard.rules.SpringAnnotations
import dev.protsenko.codeguard.rules.isSpringDataRepository

/**
 * Rules for naming conventions across Spring Boot layers.
 */
object NamingRules {
    /**
     * Rule: Classes annotated with @Service should end with "Service".
     */
    val serviceNamingRule =
        object : SpringBootRule {
            override val description = "Classes with @Service annotation should end with 'Service'"
            override val suppressKey = "CodeGuard:serviceNaming"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.SERVICE)
                    .filterNot { it.name.endsWith("Service") }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Classes with @Service annotation should end with 'Service': $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: Classes and interfaces annotated with @Repository should end with "Repository".
     */
    val repositoryNamingRule =
        object : SpringBootRule {
            override val description = "Classes and interfaces with @Repository annotation should end with 'Repository'"
            override val suppressKey = "CodeGuard:repositoryNaming"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClassesAndInterfaces(suppressKey)
                    .filter { it.isSpringDataRepository() }
                    .filterNot { it.name.endsWith("Repository") }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingRepos = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Repository interfaces should end with 'Repository': $violatingRepos",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: Classes annotated with @Controller or @RestController should end with "Controller".
     */
    val controllerNamingRule =
        object : SpringBootRule {
            override val description = "Controller classes should end with 'Controller'"
            override val suppressKey = "CodeGuard:controllerNaming"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.controllerAnnotations)
                    .filterNot { it.name.endsWith("Controller") }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Controller classes should end with 'Controller': $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: Classes annotated with @RestControllerAdvice or @ControllerAdvice should end with
     * "ExceptionHandler", "ControllerAdvice", or "Advice".
     */
    val exceptionHandlerNamingRule =
        object : SpringBootRule {
            override val description = "Exception handler classes should follow naming conventions"
            override val suppressKey = "CodeGuard:exceptionHandlerNaming"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(
                        SpringAnnotations.REST_CONTROLLER_ADVICE,
                        SpringAnnotations.CONTROLLER_ADVICE,
                    ).filterNot { klass ->
                        listOf("ExceptionHandler", "Advice")
                            .any { klass.name.endsWith(it) }
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Exception handler classes should end with 'ExceptionHandler' or 'Advice': $violatingClasses",
                            )
                        }
                    }
            }
        }

    /**
     * Rule: Classes annotated with @ConfigurationProperties should end with "Properties".
     */
    val configurationPropertiesNamingRule =
        object : SpringBootRule {
            override val description = "Classes with @ConfigurationProperties annotation should end with 'Properties'"
            override val suppressKey = "CodeGuard:configurationPropertiesNaming"

            override fun verify(scope: KoScope) {
                scope
                    .notSuppressedClasses(suppressKey)
                    .withAnnotationNamed(SpringAnnotations.CONFIGURATION_PROPERTIES)
                    .filterNot { it.name.endsWith("Properties") }
                    .also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingClasses = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Classes with @ConfigurationProperties annotation should end with 'Properties': $violatingClasses",
                            )
                        }
                    }
            }
        }
}

val allNamingRules: List<SpringBootRule> =
    listOf(
        NamingRules.serviceNamingRule,
        NamingRules.repositoryNamingRule,
        NamingRules.controllerNamingRule,
        NamingRules.exceptionHandlerNamingRule,
        NamingRules.configurationPropertiesNamingRule,
    )
