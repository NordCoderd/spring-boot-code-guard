package dev.protsenko.codeguard.core

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import dev.protsenko.codeguard.rules.general.GeneralRuleContext
import dev.protsenko.codeguard.rules.general.allCoreRules
import dev.protsenko.codeguard.rules.jpa.JpaRuleContext
import dev.protsenko.codeguard.rules.jpa.allJpaRules
import dev.protsenko.codeguard.rules.naming.NamingRuleContext
import dev.protsenko.codeguard.rules.naming.allNamingRules
import dev.protsenko.codeguard.rules.packages.PackageRuleContext
import dev.protsenko.codeguard.rules.packages.allPackageRules
import dev.protsenko.codeguard.rules.web.WebRuleContext
import dev.protsenko.codeguard.rules.web.allWebRules

/**
 * Main DSL entry point for configuring Spring Boot Konsist rules.
 */
@SpringBootRuleDsl
class SpringBootRulesConfiguration {
    private val allRules = linkedSetOf<SpringBootRule>()

    /**
     * The scope to apply rules against. Defaults to the entire project.
     */
    var scope: KoScope = Konsist.scopeFromProduction()

    /**
     * Configure Spring Core rules (DI, components, configuration).
     */
    fun general(block: GeneralRuleContext.() -> Unit) {
        val context = GeneralRuleContext()
        context.block()
        allRules.addAll(context.getRules())
    }

    /**
     * Configure Spring Web MVC rules (controllers, REST, request mappings).
     */
    fun web(block: WebRuleContext.() -> Unit) {
        val context = WebRuleContext()
        context.block()
        allRules.addAll(context.getRules())
    }

    /**
     * Configure JPA rules (entities, repositories, transactions).
     */
    fun jpa(block: JpaRuleContext.() -> Unit) {
        val context = JpaRuleContext()
        context.block()
        allRules.addAll(context.getRules())
    }

    /**
     * Configure naming and naming-convention rules.
     */
    fun naming(block: NamingRuleContext.() -> Unit) {
        val context = NamingRuleContext()
        context.block()
        allRules.addAll(context.getRules())
    }

    /**
     * Configure package structure and naming rules.
     */
    fun packages(block: PackageRuleContext.() -> Unit) {
        val context = PackageRuleContext()
        context.block()
        allRules.addAll(context.getRules())
    }

    /**
     * Verify all configured rules against the scope.
     * Runs every rule and collects all violations before throwing,
     * so the full list of problems is reported in a single error.
     */
    fun verify() {
        val failures = allRules.mapNotNull { rule ->
            try {
                rule.verify(scope)
                null
            } catch (e: AssertionError) {
                e.message
            }
        }
        if (failures.isNotEmpty()) {
            throw AssertionError(failures.joinToString("\n\n"))
        }
    }

    /**
     * Verify configured rules and collect per-rule results without throwing.
     */
    fun verifyWithResults(): List<RuleResult> =
        allRules.map { rule ->
            try {
                rule.verify(scope)
                RuleResult.Success
            } catch (error: AssertionError) {
                RuleResult.Failure(
                    message = error.message ?: "Rule failed: ${rule.description}",
                    violations = listOf(rule.description),
                )
            }
        }

    /**
     * Enable every available rule across all categories.
     * When adding a new rule, update the corresponding allXxxRules list in the rules file.
     */
    fun all() {
        allRules.addAll(allCoreRules)
        allRules.addAll(allJpaRules)
        allRules.addAll(allNamingRules)
        allRules.addAll(allPackageRules)
        allRules.addAll(allWebRules)
    }

    /**
     * Get all configured rules.
     */
    fun getAllRules(): List<SpringBootRule> = allRules.toList()
}

/**
 * Main DSL function to configure and verify Spring Boot rules.
 *
 * Example:
 * ```kotlin
 * springBootRules {
 *     scope = scopeFromProject()
 *
 *     core {
 *         noFieldInjection()
 *     }
 *
 *     web {
 *         restControllerReturnTypes()
 *     }
 *
 *     naming {
 *         controllerNamingConvention()
 *     }
 * }.verify()
 * ```
 */
fun springBootRules(block: SpringBootRulesConfiguration.() -> Unit): SpringBootRulesConfiguration {
    val config = SpringBootRulesConfiguration()
    config.block()
    return config
}
