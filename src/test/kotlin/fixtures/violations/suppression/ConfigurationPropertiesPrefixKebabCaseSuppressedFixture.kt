package fixtures.violations.suppression

import org.springframework.boot.context.properties.ConfigurationProperties

@Suppress("CodeGuard:configurationPropertiesPrefixKebabCase")
@ConfigurationProperties("appMail")
data class SuppressedPrefixProperties(
    val host: String = "localhost",
)
