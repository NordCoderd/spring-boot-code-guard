package fixtures.violations.core.dependencyinjection

import org.springframework.stereotype.Component

@Component
class ConstructorInjectionGood(
    private val someService: String
)
