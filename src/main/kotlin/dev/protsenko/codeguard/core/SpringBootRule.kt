package dev.protsenko.codeguard.core

import com.lemonappdev.konsist.api.container.KoScope

/**
 * Base interface for all Spring Boot Konsist rules.
 * Each rule can be executed against a given scope.
 */
interface SpringBootRule {
    /**
     * Execute the rule verification against the provided scope.
     * @param scope The Konsist scope to verify
     * @throws AssertionError if the rule is violated
     */
    fun verify(scope: KoScope)

    /**
     * Optional description of what this rule enforces.
     */
    val description: String
}

/**
 * Composite rule that aggregates multiple rules.
 */
class CompositeRule(
    private val rules: List<SpringBootRule>,
    override val description: String = "Composite rule",
) : SpringBootRule {
    override fun verify(scope: KoScope) {
        rules.forEach { it.verify(scope) }
    }
}

/**
 * Result of a rule execution.
 */
sealed class RuleResult {
    data object Success : RuleResult()

    data class Failure(
        val message: String,
        val violations: List<String> = emptyList(),
    ) : RuleResult()
}
