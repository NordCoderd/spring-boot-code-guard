package fixtures.violations.packages.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "config-props-in-config")
class ConfigurationPropertiesInConfigPackage // Violation: @ConfigurationProperties-only class (no @Configuration) should not be in .config package per onlyConfigurationsInConfigPackageRule
