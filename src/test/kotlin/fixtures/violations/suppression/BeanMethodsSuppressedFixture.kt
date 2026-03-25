package fixtures.violations.suppression

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Suppress("CodeGuard:beanMethodsInConfiguration")
@Component
class SuppressedComponent {
    @Bean
    fun someBean(): String = "bean"
}
