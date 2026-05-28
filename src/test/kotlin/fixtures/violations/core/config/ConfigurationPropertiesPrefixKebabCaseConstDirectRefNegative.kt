package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val INVALID_APP_PREFIX = "MyApp"

@ConfigurationProperties(prefix = INVALID_APP_PREFIX)
data class InvalidDirectRefProperties(
    val enabled: Boolean = true,
)
