package fixtures.violations.web.requestmapping

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GenericRequestMappingController {
    @RequestMapping("/api/endpoint")  // Violation: use @GetMapping instead
    fun endpoint(): String = "test"
}
