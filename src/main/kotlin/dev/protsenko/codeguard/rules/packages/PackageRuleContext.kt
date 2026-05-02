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
     * Ensure @ConfigurationProperties prefixes use lowercase kebab-case segments.
     */
    fun configurationPropertiesPrefixKebabCase() {
        builder.addRule(PackageRules.configurationPropertiesPrefixKebabCaseRule)
    }

    /**
     * Ensure @Entity classes are in .domain or .entity package.
     */
    fun entitiesInEntityPackage() {
        builder.addRule(PackageRules.entityPackageRule)
    }

    /**
     * Enforce that only @Service classes (or their file-level helpers) reside in .service package.
     */
    fun onlyServicesInServicePackage() {
        builder.addRule(PackageRules.onlyServicesInServicePackageRule)
    }

    /**
     * Enforce that only @Entity classes (or their file-level helpers) reside in .domain or .entity package.
     */
    fun onlyEntitiesInEntityPackage() {
        builder.addRule(PackageRules.onlyEntitiesInEntityPackageRule)
    }

    /**
     * Enforce that only @Controller/@RestController classes (or their file-level helpers) reside in .controller or .web package.
     */
    fun onlyControllersInControllerPackage() {
        builder.addRule(PackageRules.onlyControllersInControllerPackageRule)
    }

    /**
     * Enforce that only @Configuration classes (or their file-level helpers) reside in .config or .configuration package.
     */
    fun onlyConfigurationsInConfigPackage() {
        builder.addRule(PackageRules.onlyConfigurationsInConfigPackageRule)
    }

    /**
     * Enforce that only @ConfigurationProperties classes (or their file-level helpers) reside in .property package.
     */
    fun onlyPropertiesInPropertyPackage() {
        builder.addRule(PackageRules.onlyPropertiesInPropertyPackageRule)
    }

    /**
     * Enforce that @Repository classes and Spring Data repository interfaces reside in .repository package.
     */
    fun repositoryInRepositoryPackage() {
        builder.addRule(PackageRules.repositoryPackageRule)
    }

    /**
     * Enforce that only @Repository classes (or their file-level helpers) reside in .repository package.
     */
    fun onlyRepositoriesInRepositoryPackage() {
        builder.addRule(PackageRules.onlyRepositoriesInRepositoryPackageRule)
    }
}
