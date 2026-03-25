package fixtures.violations.web.responsehandling.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NonResponseEntityController {
    @GetMapping("/data")
    fun getData(): String {  // Violation: returns String instead of ResponseEntity
        return "data"
    }
}
