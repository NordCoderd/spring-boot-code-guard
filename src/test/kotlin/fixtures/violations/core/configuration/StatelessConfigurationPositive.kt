package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StatelessConfiguration {  // Correct: no mutable state
    @Bean
    fun someBean(): String = "test"
}
