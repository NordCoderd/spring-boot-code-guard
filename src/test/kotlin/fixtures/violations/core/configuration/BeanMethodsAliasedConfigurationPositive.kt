package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class FreemarkerConfiguration { // Correct: @Configuration aliased to avoid conflict with third-party Configuration class
    @Bean
    fun freemarkerConfig(): String = "config"
}
