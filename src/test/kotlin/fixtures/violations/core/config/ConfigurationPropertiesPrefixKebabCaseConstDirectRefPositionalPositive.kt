package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val POSITIONAL_APP_PREFIX = "my-app"

@ConfigurationProperties(POSITIONAL_APP_PREFIX)
data class PositionalDirectRefProperties(
    val enabled: Boolean = true,
)
