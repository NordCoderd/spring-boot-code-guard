package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProperBeanConfiguration {
    @Bean
    fun someBean(): String = "test"  // Correct: @Bean in @Configuration class
}
