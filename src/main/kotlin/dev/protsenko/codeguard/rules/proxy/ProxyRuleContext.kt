package dev.protsenko.codeguard.rules.proxy

import dev.protsenko.codeguard.core.RuleContext
import dev.protsenko.codeguard.core.SpringBootRuleDsl

/**
 * Context for configuring Spring proxy rules.
 */
@SpringBootRuleDsl
class ProxyRuleContext : RuleContext() {
    /**
     * Prohibit self-invocation between proxy-annotated methods of the same class.
     * Spring AOP proxies cannot intercept calls made through `this`, so the inner annotation
     * is silently ignored (no transaction boundary, no caching, no async dispatch).
     */
    fun noSelfInvocationOfProxyMethods() {
        builder.addRule(ProxyRules.noSelfInvocationOfProxyMethodsRule)
    }
}
