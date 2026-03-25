package fixtures.violations.naming

import org.springframework.boot.context.properties.ConfigurationProperties

@Suppress("CodeGuard:configurationPropertiesNaming")
@ConfigurationProperties(prefix = "mail")
class SuppressedMailConfig {
    var host: String = ""
}
