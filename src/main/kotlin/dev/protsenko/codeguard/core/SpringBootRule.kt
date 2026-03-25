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

    /**
     * The suppress key used to opt out of this rule via @Suppress("CodeGuard:...").
     * Example: @Suppress("CodeGuard:noFieldInjection")
     */
    val suppressKey: String
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
