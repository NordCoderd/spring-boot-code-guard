package fixtures.violations.suppression

import org.springframework.context.annotation.Configuration

@Suppress("CodeGuard:statelessConfiguration")
@Configuration
class SuppressedMixedConfig {
    var counter: Int = 0
}

@Configuration
class ViolatingConfig {
    var mutableProp: String = ""
}
