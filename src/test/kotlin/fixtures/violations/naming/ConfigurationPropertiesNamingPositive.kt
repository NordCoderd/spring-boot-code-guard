package fixtures.violations.naming

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "mail")
class MailProperties {
    var host: String = ""
    var port: Int = 25
}
