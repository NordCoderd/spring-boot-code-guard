package fixtures.violations.packages.config

import org.springframework.context.annotation.Configuration

data class ConfigHelper(
    val value: String,
)

@Configuration
class ConfigurationWithHelper // Correct: @Configuration with helper data class in the same file
