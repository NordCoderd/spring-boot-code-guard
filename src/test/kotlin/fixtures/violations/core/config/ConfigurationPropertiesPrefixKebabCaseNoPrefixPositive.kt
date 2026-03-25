package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
data class NoPrefixProperties(
    val enabled: Boolean = true,
)
