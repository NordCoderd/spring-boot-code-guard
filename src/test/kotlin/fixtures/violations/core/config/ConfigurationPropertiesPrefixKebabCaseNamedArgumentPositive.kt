package fixtures.violations.core.config.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app-mail.client2")
data class ClientProperties(
    val baseUrl: String = "https://example.test",
)
