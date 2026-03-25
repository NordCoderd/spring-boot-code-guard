package fixtures.violations.web.controller.positive

@org.springframework.web.bind.annotation.RestController
class SearchController {
    @org.springframework.web.bind.annotation.GetMapping("/search")
    fun search(
        @jakarta.validation.Valid
        @org.springframework.web.bind.annotation.RequestParam
        query: String,
    ): String = query
}
