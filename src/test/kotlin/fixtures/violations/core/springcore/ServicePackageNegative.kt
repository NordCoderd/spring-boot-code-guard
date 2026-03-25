package fixtures.violations.core.springcore

import org.springframework.stereotype.Service

@Service
class ServiceNotInServicePackage {  // Violation: not in .service package
    fun process() = "test"
}
