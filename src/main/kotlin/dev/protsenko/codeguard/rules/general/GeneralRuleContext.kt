package dev.protsenko.codeguard.rules.general

import dev.protsenko.codeguard.core.RuleContext
import dev.protsenko.codeguard.core.SpringBootRuleDsl

/**
 * Context for configuring Spring Core rules.
 */
@SpringBootRuleDsl
class GeneralRuleContext : RuleContext() {
    /**
     * Prohibit field-based dependency injection.
     * No @Autowired or @Inject annotations should be used on fields.
     */
    fun noFieldInjection() {
        builder.addRule(CoreRules.noFieldInjectionRule)
    }

    /**
     * Ensure @Configuration classes don't have state (no mutable properties).
     */
    fun statelessConfiguration() {
        builder.addRule(CoreRules.statelessConfigurationRule)
    }

    /**
     * Ensure @Bean methods are in @Configuration classes.
     */
    fun beanMethodsInConfiguration() {
        builder.addRule(CoreRules.beanMethodsInConfigurationRule)
    }

    /**
     * Enforce proper structure for custom exceptions.
     */
    fun customExceptionStructure() {
        builder.addRule(CoreRules.customExceptionStructureRule)
    }

    /**
     * Enforce using logger instead of println.
     */
    fun useLoggerNotPrintln() {
        builder.addRule(CoreRules.loggerInsteadOfPrintRule)
    }

    /**
     * Enforce using logger instead of direct printStackTrace calls.
     */
    fun noStackTracePrint() {
        builder.addRule(CoreRules.noStackTracePrintRule)
    }

    /**
     * Prohibit Spring proxy annotations (@Transactional, @Cacheable, @CacheEvict, @CachePut, @Async)
     * on private methods — the proxy cannot intercept them and the annotation is silently ignored.
     */
    fun noProxyAnnotationsOnPrivateMethods() {
        builder.addRule(CoreRules.noProxyAnnotationsOnPrivateMethodsRule)
    }
}
