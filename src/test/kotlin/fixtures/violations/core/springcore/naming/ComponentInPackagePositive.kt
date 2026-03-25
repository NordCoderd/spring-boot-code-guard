package fixtures.violations.core.springcore.naming.component

import org.springframework.stereotype.Component

@Component
class Processor {  // Correct: in .component package, any name is allowed
    fun process() = "processing"
}
