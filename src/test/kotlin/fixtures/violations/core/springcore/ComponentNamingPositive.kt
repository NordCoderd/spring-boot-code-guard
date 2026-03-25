package fixtures.violations.core.springcore

import org.springframework.stereotype.Service

@Service
class GoodNameService {  // Correct: ends with "Service"
    fun doSomething() = "test"
}
