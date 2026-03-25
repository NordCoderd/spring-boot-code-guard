package fixtures.violations.core.configuration.config.negative

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    fun configuredMethod(): String = "ok"
}

class AppConfigHelper {
    @Bean
    fun helperBean(): String = "wrong-place"
}
