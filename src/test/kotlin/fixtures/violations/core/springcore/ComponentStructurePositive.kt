package fixtures.violations.core.springcore.component

import org.springframework.stereotype.Component

@Component
class ProperComponent {  // Correct: in .component package
    fun handle() = "test"
}
