package fixtures.violations.core

import org.springframework.stereotype.Component

@Component
class Handler {  // Violation: generic @Component not in .component package, no proper suffix
    fun handle() = "test"
}
