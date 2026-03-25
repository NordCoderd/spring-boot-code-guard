package fixtures.violations.web.requestmapping

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NoTrailingSlashController {
    @GetMapping("/api/test")  // Correct: no trailing slash
    fun noTrailingSlash(): String = "test"
}
