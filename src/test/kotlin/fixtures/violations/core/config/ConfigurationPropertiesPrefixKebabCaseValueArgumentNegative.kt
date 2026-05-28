package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(value = "appMail")
data class ValueArgumentProperties(
    val host: String = "localhost",
)
