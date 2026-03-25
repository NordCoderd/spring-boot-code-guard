package fixtures.violations.web.responsehandling.controller

import fixtures.violations.web.responsehandling.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MapEntityController {
    @GetMapping("/users/map")
    fun getUserMap(): Map<String, User> = mapOf("u" to User())
}
