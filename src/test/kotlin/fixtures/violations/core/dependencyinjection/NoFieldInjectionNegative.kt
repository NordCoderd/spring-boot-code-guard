package fixtures.violations.core.dependencyinjection

import org.springframework.beans.factory.annotation.Autowired

class ServiceWithFieldInjection {
    @Autowired
    lateinit var repository: String
}
