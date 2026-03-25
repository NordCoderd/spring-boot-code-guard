package fixtures.violations.web.responsehandling.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ResponseEntityController {
    @GetMapping("/data")
    fun getData(): ResponseEntity<String> {  // Correct: returns ResponseEntity
        return ResponseEntity("data")
    }
}
