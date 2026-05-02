package fixtures.violations.packages.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class ProperPropertiesAlone // Correct: @ConfigurationProperties alone in .property package
