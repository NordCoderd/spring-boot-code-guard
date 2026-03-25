package fixtures.violations.naming

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "mail")
class MailConfig {
    var host: String = ""
    var port: Int = 25
}
