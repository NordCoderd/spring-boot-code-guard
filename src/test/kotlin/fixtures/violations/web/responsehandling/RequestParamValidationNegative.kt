package fixtures.violations.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController {
    @GetMapping("/search")
    fun search(
        @org.springframework.web.bind.annotation.RequestParam query: String,
    ): String = query
}
