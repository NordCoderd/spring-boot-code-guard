package fixtures.violations.packages.property

import org.springframework.boot.context.properties.ConfigurationProperties

data class NestedProperties(
    val value: String,
)

@ConfigurationProperties(prefix = "app")
class PropertiesWithHelper // Correct: @ConfigurationProperties with helper data class in the same file
