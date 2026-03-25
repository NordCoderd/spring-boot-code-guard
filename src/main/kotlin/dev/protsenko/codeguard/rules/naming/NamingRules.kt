package dev.protsenko.codeguard.rules.naming

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.rules.SpringAnnotations

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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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

            override fun verify(scope: KoScope) {
                scope
                    .classesAndInterfaces()
                    .withAnnotationNamed(SpringAnnotations.REPOSITORY)
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
     * Rule: Classes annotated with @Component should follow naming conventions.
     */
    val componentNamingRule =
        object : SpringBootRule {
            override val description = "Classes with @Component annotation should follow naming conventions"

            override fun verify(scope: KoScope) {
                scope
                    .classes()
                    .withAnnotationNamed(SpringAnnotations.COMPONENT)
                    .filterNot { it.resideInPackage("..component..") }
                    .filterNot { klass ->
                        listOf("Component", "Factory", "Provider", "Listener", "Handler")
                            .any { klass.name.endsWith(it) }
                    }.also { violations ->
                        if (violations.isNotEmpty()) {
                            val violatingComponents = violations.joinToString(", ") { it.name }
                            throw AssertionError(
                                "Classes with @Component annotation should end with 'Component', 'Factory', 'Provider', 'Listener', or 'Handler': $violatingComponents",
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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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

            override fun verify(scope: KoScope) {
                scope
                    .classes()
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
}

val allNamingRules: List<SpringBootRule> = listOf(
    NamingRules.serviceNamingRule,
    NamingRules.repositoryNamingRule,
    NamingRules.componentNamingRule,
    NamingRules.controllerNamingRule,
    NamingRules.exceptionHandlerNamingRule,
)
