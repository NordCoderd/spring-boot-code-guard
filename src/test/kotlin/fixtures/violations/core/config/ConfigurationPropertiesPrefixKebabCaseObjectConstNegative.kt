package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

object InvalidConfigConstants {
    const val INVALID_OBJECT_KEY = "MyApp"
}

@ConfigurationProperties(prefix = InvalidConfigConstants.INVALID_OBJECT_KEY)
data class InvalidObjectConstProperties(
    val enabled: Boolean = true,
)
