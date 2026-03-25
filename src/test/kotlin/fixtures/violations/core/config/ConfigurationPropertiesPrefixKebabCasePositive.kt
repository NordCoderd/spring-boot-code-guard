package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.mail")
data class MailProperties(
    val host: String = "localhost",
)
