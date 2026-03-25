package fixtures.violations.core.configuration.config.positive

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    @Bean
    fun appBean(): String = "ok"
}

class AppConfigHelper {
    fun utility(): String = "helper"
}
