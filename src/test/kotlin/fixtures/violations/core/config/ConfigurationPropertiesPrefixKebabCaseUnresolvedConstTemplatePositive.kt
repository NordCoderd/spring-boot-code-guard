package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("$APP_NAME")
data class UnresolvedAppNameProperties(
    val enabled: Boolean = true,
)
