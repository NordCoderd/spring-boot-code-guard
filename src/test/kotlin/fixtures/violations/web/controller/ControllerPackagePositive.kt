package fixtures.violations.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProperController {  // Correct: in .controller package
    @GetMapping("/data")
    fun getData(): String = "data"
}
