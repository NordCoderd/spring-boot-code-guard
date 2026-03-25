package dev.protsenko.codeguard.rules.naming

import dev.protsenko.codeguard.core.RuleContext
import dev.protsenko.codeguard.core.SpringBootRuleDsl

/**
 * Context for configuring naming-related rules.
 */
@SpringBootRuleDsl
class NamingRuleContext : RuleContext() {
    /**
     * Enforce naming convention for service classes (must end with "Service").
     */
    fun serviceNamingConvention() {
        builder.addRule(NamingRules.serviceNamingRule)
    }

    /**
     * Enforce naming conventions for @Component classes.
     */
    fun componentNamingConvention() {
        builder.addRule(NamingRules.componentNamingRule)
    }

    /**
     * Enforce controller naming convention (must end with "Controller").
     */
    fun controllerNamingConvention() {
        builder.addRule(NamingRules.controllerNamingRule)
    }

    /**
     * Enforce the repository naming convention (must end with "Repository").
     */
    fun repositoryNamingConvention() {
        builder.addRule(NamingRules.repositoryNamingRule)
    }

    /**
     * Enforce naming convention for exception handler classes.
     */
    fun exceptionHandlerNaming() {
        builder.addRule(NamingRules.exceptionHandlerNamingRule)
    }
}
