package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = UNRESOLVED_DIRECT_CONST)
data class UnresolvedDirectRefProperties(
    val enabled: Boolean = true,
)
