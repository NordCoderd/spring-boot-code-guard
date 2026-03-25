package dev.protsenko.codeguard.rules.jpa

import dev.protsenko.codeguard.core.RuleContext
import dev.protsenko.codeguard.core.SpringBootRuleDsl

/**
 * Context for configuring JPA and persistence rules.
 */
@SpringBootRuleDsl
class JpaRuleContext : RuleContext() {
    /**
     * Ensure @Entity classes have proper @Id annotation.
     */
    fun entitiesHaveIdField() {
        builder.addRule(JpaRules.entityIdRule)
    }

    /**
     * Ensure @Entity classes are not data classes (incompatible with JPA).
     */
    fun noDataClassEntities() {
        builder.addRule(JpaRules.noDataClassEntityRule)
    }

    /**
     * Ensure @Transactional is on the service layer, not controllers.
     */
    fun transactionalOnServiceLayer() {
        builder.addRule(JpaRules.transactionalPlacementRule)
    }

    /**
     * Enforce clean architecture: domain layer has no framework dependencies.
     */
    fun domainLayerNoDependencies() {
        builder.addRule(JpaRules.domainLayerIndependenceRule)
    }
}
