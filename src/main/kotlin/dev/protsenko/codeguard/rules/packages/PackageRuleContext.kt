package dev.protsenko.codeguard.rules.packages

import dev.protsenko.codeguard.core.RuleContext
import dev.protsenko.codeguard.core.SpringBootRuleDsl

/**
 * Context for configuring package-related rules.
 */
@SpringBootRuleDsl
class PackageRuleContext : RuleContext() {
    /**
     * Enforce package naming conventions.
     */
    fun packageNamingConvention() {
        builder.addRule(PackageRules.packageNamingRule)
    }

    /**
     * Enforce service package conventions.
     */
    fun serviceInServicePackage() {
        builder.addRule(PackageRules.servicePackageRule)
    }

    /**
     * Enforce controller package conventions.
     */
    fun controllerInControllerPackage() {
        builder.addRule(PackageRules.controllerPackageRule)
    }

    /**
     * Enforce that @Configuration classes are in .config/.configuration packages.
     */
    fun configurationInConfigPackage() {
        builder.addRule(PackageRules.configurationPackageRule)
    }

    /**
     * Ensure @ConfigurationProperties classes are in .properties package.
     */
    fun configurationPropertiesInPropertiesPackage() {
        builder.addRule(PackageRules.propertiesValidationRule)
    }

    /**
     * Ensure @Entity classes are in .domain or .entity package.
     */
    fun entitiesInEntityPackage() {
        builder.addRule(PackageRules.entityPackageRule)
    }
}
