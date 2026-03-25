package fixtures.violations.core.springcore.naming

import org.springframework.stereotype.Component

@Component
class BadName {  // Violation: doesn't end with Component/Factory/Provider/Listener/Handler
    fun process() = "processing"
}
