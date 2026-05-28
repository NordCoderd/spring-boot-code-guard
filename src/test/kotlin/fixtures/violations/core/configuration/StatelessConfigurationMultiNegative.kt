package fixtures.violations.core.configuration.config

import org.springframework.context.annotation.Configuration

@Configuration
class StatefulConfigAlpha {
    var counter: Int = 0
}

@Configuration
class StatefulConfigBeta {
    var flag: Boolean = false
}
