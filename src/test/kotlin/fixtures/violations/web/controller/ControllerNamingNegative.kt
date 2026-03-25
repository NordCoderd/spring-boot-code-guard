package fixtures.violations.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BadApi {  // Violation: doesn't end with "Controller"
    @GetMapping("/test")
    fun getTest(): String = "test"
}
