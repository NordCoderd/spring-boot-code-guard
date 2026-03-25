package fixtures.violations.core.configuration.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppConfigurationProperties {  // Correct: @ConfigurationProperties on the class exempts it — var fields are valid for binding
    var host: String = ""
    var port: Int = 8080
}
