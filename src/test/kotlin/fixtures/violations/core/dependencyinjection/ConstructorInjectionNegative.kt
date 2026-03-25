package fixtures.violations.core.dependencyinjection

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FieldInjectionViolation {
    @Autowired
    lateinit var someService: String
}
