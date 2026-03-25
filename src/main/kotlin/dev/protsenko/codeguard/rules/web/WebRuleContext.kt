package dev.protsenko.codeguard.rules.web

import dev.protsenko.codeguard.core.RuleContext
import dev.protsenko.codeguard.core.SpringBootRule
import dev.protsenko.codeguard.core.SpringBootRuleDsl

val allWebRules: List<SpringBootRule> = listOf(
    ResponseHandlingRules.restControllerReturnTypeRule,
    RequestHandlingRules.httpMethodAnnotationRule,
    RequestHandlingRules.noTrailingSlashRule,
    ResponseHandlingRules.dtoSeparationRule,
    RequestHandlingRules.requestValidationRule,
    ControllerUsingRules.controllerRepositoryRule,
)

/**
 * Context for configuring Spring Web MVC rules.
 */
@SpringBootRuleDsl
class WebRuleContext : RuleContext() {
    /**
     * Ensure @RestController methods return appropriate types (not void for GET).
     */
    fun restControllerReturnTypes() {
        builder.addRule(ResponseHandlingRules.restControllerReturnTypeRule)
    }

    /**
     * Ensure controller methods use proper HTTP method annotations.
     */
    fun properHttpMethodAnnotations() {
        builder.addRule(RequestHandlingRules.httpMethodAnnotationRule)
    }

    /**
     * Ensure @RequestMapping paths don't have trailing slashes.
     */
    fun noTrailingSlashesInPaths() {
        builder.addRule(RequestHandlingRules.noTrailingSlashRule)
    }

    /**
     * Ensure DTOs are separate from entities.
     */
    fun separateDtosFromEntities() {
        builder.addRule(ResponseHandlingRules.dtoSeparationRule)
    }

    /**
     * Ensure @RequestBody parameters have validation annotations.
     */
    fun validateRequestBody() {
        builder.addRule(RequestHandlingRules.requestValidationRule)
    }

    /**
     * Enforce that controllers don't directly access repositories.
     */
    fun controllersDoNotAccessRepositories() {
        builder.addRule(ControllerUsingRules.controllerRepositoryRule)
    }
}
