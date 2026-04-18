package fixtures.violations.packages.service

import org.springframework.stereotype.Service

@Service
class ProperService {  // Correct: @Service alone in .service package
    fun execute() = Unit
}
