package fixtures.violations.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProperReturnTypeController {
    @GetMapping("/data")
    fun getData(): String {  // Correct: returns proper type
        return "test"
    }
}
