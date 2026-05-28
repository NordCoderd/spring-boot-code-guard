package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Bean

class NotConfigClassAlpha {
    @Bean
    fun beanAlpha(): String = "alpha"
}

class NotConfigClassBeta {
    @Bean
    fun beanBeta(): String = "beta"
}
