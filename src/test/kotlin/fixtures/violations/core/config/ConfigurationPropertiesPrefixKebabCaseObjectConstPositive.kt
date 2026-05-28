package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

object AppConfigConstants {
    const val OBJECT_APP_KEY = "my-app"
}

@ConfigurationProperties(prefix = AppConfigConstants.OBJECT_APP_KEY)
data class ObjectConstProperties(
    val enabled: Boolean = true,
)
