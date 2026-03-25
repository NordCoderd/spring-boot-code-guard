package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Bean

// Bean outside Configuration
class NotAConfigClass {
    @Bean
    fun someBean(): String = "test"  // Violation: @Bean not in @Configuration class
}
