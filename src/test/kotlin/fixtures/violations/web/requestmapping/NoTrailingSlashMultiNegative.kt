package fixtures.violations.web.requestmapping

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MultiTrailingSlashController {
    @GetMapping("/api/alpha/")
    fun alpha(): String = "alpha"

    @PostMapping("/api/beta/")
    fun beta(): String = "beta"
}
