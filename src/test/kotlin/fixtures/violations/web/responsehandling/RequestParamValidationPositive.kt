package fixtures.violations.web.controller.positive

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestParam

@org.springframework.web.bind.annotation.RestController
class SearchController {
    @org.springframework.web.bind.annotation.GetMapping("/search")
    fun search(
        @Valid
        @RequestParam
        query: String,
    ): String = query
}
