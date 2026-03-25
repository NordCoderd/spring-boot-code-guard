package fixtures.violations.web.requestmapping

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TrailingSlashController {
    @GetMapping("/api/test/")  // Violation: trailing slash
    fun trailingSlash(): String = "test"
}
