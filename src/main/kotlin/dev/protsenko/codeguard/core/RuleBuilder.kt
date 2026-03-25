package dev.protsenko.codeguard.core

/**
 * Builder for configuring and composing Spring Boot rules.
 */
class RuleBuilder {
    private val rules = mutableListOf<SpringBootRule>()

    /**
     * Add a single rule to the builder.
     */
    fun addRule(rule: SpringBootRule) {
        rules.add(rule)
    }

    /**
     * Add multiple rules to the builder.
     */
    fun addRules(vararg newRules: SpringBootRule) {
        rules.addAll(newRules)
    }

    /**
     * Build a composite rule from all added rules.
     */
    fun build(): SpringBootRule = CompositeRule(rules.toList(), "Combined Spring Boot Rules")

    /**
     * Get all configured rules.
     */
    fun getRules(): List<SpringBootRule> = rules.toList()
}

/**
 * DSL marker for Spring Boot rule configuration.
 */
@DslMarker
annotation class SpringBootRuleDsl

/**
 * Context for configuring rules within a specific category.
 */
@SpringBootRuleDsl
abstract class RuleContext {
    protected val builder = RuleBuilder()

    /**
     * Get all rules from this context.
     */
    fun getRules(): List<SpringBootRule> = builder.getRules()
}
