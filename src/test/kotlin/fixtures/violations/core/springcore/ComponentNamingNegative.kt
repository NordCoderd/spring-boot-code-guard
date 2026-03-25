package fixtures.violations.core.springcore

import org.springframework.stereotype.Service

@Service
class BadServiceName {  // Violation: doesn't end with "Service"
    fun doSomething() = "test"
}
