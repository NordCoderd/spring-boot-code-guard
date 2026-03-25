package fixtures.violations.core.springcore.service

import org.springframework.stereotype.Service

@Service
class ProperService {  // Correct: in .service package
    fun process() = "test"
}
