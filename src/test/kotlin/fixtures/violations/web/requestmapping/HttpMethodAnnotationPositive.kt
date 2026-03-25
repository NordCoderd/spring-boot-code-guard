package fixtures.violations.web.requestmapping

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SpecificHttpMethodController {
    @GetMapping("/api/endpoint")  // Correct: uses specific @GetMapping
    fun endpoint(): String = "test"
}
