package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Configuration

@Configuration
class StatefulConfiguration {  // Violation: has mutable state
    var counter: Int = 0
}
