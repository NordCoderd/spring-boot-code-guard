package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

const val MY_APP_PREFIX = "my-app"

@ConfigurationProperties(prefix = MY_APP_PREFIX)
data class MyAppProperties(
    val enabled: Boolean = true,
)
