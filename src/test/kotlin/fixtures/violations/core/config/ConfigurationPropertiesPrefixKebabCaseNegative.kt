package fixtures.violations.core.config.prefix

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("appMail")
data class AppProperties(
    val host: String = "localhost",
)
