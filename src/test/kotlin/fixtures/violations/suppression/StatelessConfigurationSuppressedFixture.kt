package fixtures.violations.suppression

import org.springframework.context.annotation.Configuration

@Suppress("CodeGuard:statelessConfiguration")
@Configuration
class SuppressedStatelessConfig {
    var counter: Int = 0
}
